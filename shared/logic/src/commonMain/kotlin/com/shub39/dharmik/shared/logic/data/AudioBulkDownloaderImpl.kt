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

import com.shub39.dharmik.shared.core.domain.AudioBulkRepo
import com.shub39.dharmik.shared.core.domain.AudioManifest
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.BulkAudioState
import com.shub39.dharmik.shared.core.domain.GITA_AUDIO_MANIFEST_URL
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.HashingSink
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
        } catch (e: CancellationException) {
            cleanupArchive()
            _state.value = BulkAudioState.Failed(null)
            throw e
        } catch (e: Exception) {
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

    private suspend fun downloadArchive(remote: AudioManifest) {
        store.ensureRoot()
        val part = store.zipPartFile
        if (fs.exists(part)) fs.delete(part)

        client.prepareGet(remote.url).execute { response ->
            val total = remote.sizeBytes
            val fileSink = fs.sink(part)
            val hashingSink = HashingSink.sha256(fileSink)
            val sink = hashingSink.buffer()

            val channel = response.bodyAsChannel()
            val buffer = ByteArray(BUFFER_SIZE)
            var downloaded = 0L

            try {
                while (true) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read == -1) break
                    sink.write(buffer, 0, read)
                    downloaded += read
                    _state.value = BulkAudioState.Downloading(downloaded, total)
                }
                sink.flush()
            } finally {
                sink.close()
            }

            val digest = hashingSink.hash.hex()
            if (remote.sha256.isNotBlank() && !digest.equals(remote.sha256, ignoreCase = true)) {
                error("Audio archive checksum mismatch")
            }
        }
    }

    private fun extractArchive() {
        _state.value = BulkAudioState.Extracting
        val zip = store.zipFile

        // Replace semantics: the whole set supersedes any per-verse files.
        store.deleteAll()
        store.ensureRoot()
        fs.atomicMove(store.zipPartFile, zip)

        extractZip(zip.toString()) { entryPath, bytes ->
            val parsed = parseEntry(entryPath)
            if (parsed != null) {
                val dest = store.targetFile(parsed.chapter, parsed.verse, parsed.variant, "ogg")
                fs.createDirectories(dest.parent!!)
                fs.write(dest) { write(bytes) }
            }
        }

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
        const val BUFFER_SIZE = 64 * 1024
    }
}
