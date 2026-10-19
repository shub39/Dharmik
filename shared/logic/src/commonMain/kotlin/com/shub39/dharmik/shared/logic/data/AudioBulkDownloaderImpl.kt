/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.logic.data

import com.shub39.dharmik.shared.core.DharmikLogger
import com.shub39.dharmik.shared.core.domain.AudioBulkRepo
import com.shub39.dharmik.shared.core.domain.AudioManifest
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.BulkAudioState
import com.shub39.dharmik.shared.core.domain.GITA_AUDIO_MANIFEST_URL
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.readAvailable
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.buffer
import org.koin.core.annotation.Single

/**
 * App-scoped orchestrator for the optional whole-set audio download.
 *
 * [start] hands off to [AudioDownloadStarter] (an Android foreground service) and the actual work
 * runs in [run], which the starter drives. Progress is published through [state].
 */
@Single(binds = [AudioBulkRepo::class])
class AudioBulkDownloaderImpl(
    private val store: AudioStore,
    private val client: HttpClient,
    private val preferences: PreferencesRepo,
    private val starter: AudioDownloadStarter,
) : AudioBulkRepo {

    private val fs: FileSystem = FileSystem.SYSTEM
    private val json = Json { ignoreUnknownKeys = true }

    private val _state = MutableStateFlow<BulkAudioState>(BulkAudioState.Checking)
    override val state: Flow<BulkAudioState> = _state.asStateFlow()

    private var manifest: AudioManifest? = null
    private var downloadJob: Job? = null

    override suspend fun refresh() {
        _state.value = BulkAudioState.Checking
        val localVersion = preferences.getBulkAudioVersion().first()
        try {
            val remote = fetchManifest()
            manifest = remote
            _state.value =
                when {
                    localVersion == null ->
                        BulkAudioState.NotDownloaded(remote.version, remote.sizeBytes)
                    localVersion != remote.version ->
                        BulkAudioState.UpdateAvailable(remote.version, remote.sizeBytes)
                    else -> BulkAudioState.Downloaded(localVersion)
                }
        } catch (e: Exception) {
            _state.value =
                if (localVersion != null) BulkAudioState.Downloaded(localVersion)
                else BulkAudioState.NotDownloaded(null, null)
        }
    }

    override suspend fun start() {
        refresh()
        if (manifest == null) return
        starter.start()
    }

    override suspend fun cancel() {
        downloadJob?.cancel()
    }

    override suspend fun delete() {
        downloadJob?.cancel()
        store.deleteAll()
        preferences.setBulkAudioVersion(null)
        refresh()
    }

    /** Drives the whole download + extraction. Invoked by the platform starter. */
    suspend fun run() {
        if (downloadJob?.isActive == true) return
        val remote = manifest ?: return

        downloadJob = currentCoroutineContext()[Job]
        try {
            downloadArchive(remote)
            extractArchive()
            preferences.setBulkAudioVersion(remote.version)
            _state.value = BulkAudioState.Downloaded(remote.version)
            DharmikLogger.d(TAG, "Bulk audio download complete: ${remote.version}")
        } catch (e: CancellationException) {
            cleanupArchive()
            _state.value = BulkAudioState.Failed(null)
            throw e
        } catch (e: Exception) {
            DharmikLogger.e(TAG, "Bulk audio download failed", e)
            cleanupArchive()
            _state.value = BulkAudioState.Failed(e.message)
        } finally {
            downloadJob = null
        }
    }

    private suspend fun fetchManifest(): AudioManifest {
        val body = client.get(GITA_AUDIO_MANIFEST_URL).bodyAsBytes().decodeToString()
        return json.decodeFromString(body)
    }

    /**
     * Downloads the archive to [AudioStore.zipPartFile], resuming across attempts.
     *
     * The archive is large, so a flaky connection can drop mid-transfer. Each attempt keeps the
     * bytes already written and resumes with an HTTP `Range` request; only after
     * [MAX_DOWNLOAD_ATTEMPTS] failures does it give up. The checksum is verified over the completed
     * file.
     */
    private suspend fun downloadArchive(remote: AudioManifest) {
        store.ensureRoot()
        val part = store.zipPartFile
        if (fs.exists(part)) fs.delete(part)

        var attempt = 0
        while (true) {
            val existing = if (fs.exists(part)) fs.metadata(part).size ?: 0L else 0L
            try {
                streamArchive(remote, part, existing)
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                attempt++
                DharmikLogger.e(
                    TAG,
                    "Download attempt $attempt stalled/failed at " +
                        "$existing/${remote.sizeBytes} bytes",
                    e,
                )
                if (attempt >= MAX_DOWNLOAD_ATTEMPTS) throw e
                // Keep what we have and resume from there once the connection recovers.
                _state.value = BulkAudioState.Downloading(existing, remote.sizeBytes)
                delay((RETRY_BACKOFF_MS * attempt).milliseconds)
            }
        }

        if (remote.sha256.isNotBlank()) {
            val digest = sha256Of(part)
            if (!digest.equals(remote.sha256, ignoreCase = true)) {
                fs.delete(part)
                error("Audio archive checksum mismatch")
            }
        }
    }

    /** Streams (the remainder of) the archive into [part], resuming via an HTTP Range request. */
    private suspend fun streamArchive(remote: AudioManifest, part: Path, existing: Long) {
        client
            .prepareGet(remote.url) {
                if (existing > 0L) header(HttpHeaders.Range, "bytes=$existing-")
            }
            .execute { response ->
                // If we asked to resume but the server ignored the Range header, start over.
                val resumeFrom =
                    if (existing > 0L && response.status == HttpStatusCode.PartialContent) existing
                    else 0L
                if (resumeFrom == 0L && existing > 0L) fs.delete(part)

                val sink = fs.appendingSink(part).buffer()
                val channel = response.bodyAsChannel()
                val buffer = ByteArray(BUFFER_SIZE)
                var downloaded = resumeFrom
                var lastPercent = -1

                try {
                    while (true) {
                        val read = channel.readAvailable(buffer, 0, buffer.size)
                        if (read == -1) break
                        if (read == 0) continue
                        sink.write(buffer, 0, read)
                        downloaded += read
                        lastPercent = publishProgress(downloaded, remote.sizeBytes, lastPercent)
                    }
                    sink.flush()
                } finally {
                    sink.close()
                }
            }
    }

    /**
     * Publishes download progress at 1% granularity.
     *
     * Updating state on every buffer read floods the foreground-service notification (the system
     * rate-limits updates) and the UI, so only emit when the whole percentage changes.
     */
    private fun publishProgress(downloaded: Long, total: Long, lastPercent: Int): Int {
        val percent = if (total > 0L) ((downloaded * 100) / total).toInt().coerceIn(0, 100) else 100
        if (percent != lastPercent) {
            _state.value = BulkAudioState.Downloading(downloaded, total)
        }
        return percent
    }

    /** Streaming SHA-256 of [path], used to verify the completed archive. */
    private fun sha256Of(path: Path): String {
        val hashingSink = HashingSink.sha256(blackholeSink())
        val buffered = hashingSink.buffer()
        fs.source(path).use { buffered.writeAll(it) }
        buffered.flush()
        return hashingSink.hash.hex()
    }

    private fun extractArchive() {
        _state.value = BulkAudioState.Extracting
        val zip = store.zipFile

        // Move the downloaded archive into place first: it lives in the same directory as the audio
        // it replaces, so it must not be removed before being read.
        fs.atomicMove(store.zipPartFile, zip)

        extractZip(zip.toString()) { entryPath, bytes ->
            val parsed = parseEntry(entryPath)
            if (parsed != null) {
                val dest = store.targetFile(parsed.chapter, parsed.verse, parsed.variant, "ogg")
                fs.createDirectories(dest.parent!!)
                fs.write(dest) { write(bytes) }
            }
        }

        // Replace semantics: the whole set supersedes any per-verse files, so drop the stale `.mp3`
        // copies now that the `.ogg` set is on disk.
        store.deletePerVerseFiles()
        fs.delete(zip)
    }

    private fun cleanupArchive() {
        val part = store.zipPartFile
        if (fs.exists(part)) fs.delete(part)
    }

    private fun parseEntry(entryPath: String): ParsedEntry? {
        val parts = entryPath.removePrefix("./").split("/")
        if (parts.size != 2 || !parts[0].startsWith("CHAP")) return null

        val chapter = parts[0].removePrefix("CHAP").toLongOrNull() ?: return null
        val stem = parts[1].substringBeforeLast('.')
        val verse = stem.substringBefore('-').toLongOrNull() ?: return null
        val bulkName = stem.substringAfter('-', "")
        val variant = AudioVariant.entries.firstOrNull { it.bulkName == bulkName } ?: return null

        return ParsedEntry(chapter, verse, variant)
    }

    private data class ParsedEntry(val chapter: Long, val verse: Long, val variant: AudioVariant)

    private companion object {
        const val TAG = "AudioBulkDownloader"
        const val BUFFER_SIZE = 64 * 1024
        const val MAX_DOWNLOAD_ATTEMPTS = 10
        const val RETRY_BACKOFF_MS = 1_000L
    }
}
