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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import com.shub39.dharmik.shared.core.domain.BulkAudioState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Foreground service that keeps the process alive while the (large) bulk audio download runs. The
 * download itself is app-scoped in [AudioBulkDownloaderImpl]; this service only owns the progress
 * notification and the elevated process priority.
 */
class AudioDownloadService : Service(), KoinComponent {

    private val downloader: AudioBulkDownloaderImpl by inject()
    // The download + extraction do blocking file I/O, so keep it off the main thread.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        startForegroundIfNeeded()

        downloader.state.onEach { state -> render(state) }.launchIn(scope)

        scope.launch { downloader.run() }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun startForegroundIfNeeded() {
        val notification = buildNotification(0f)
        startForeground(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    private fun render(state: BulkAudioState) {
        when (state) {
            is BulkAudioState.Downloading -> notify(state.progress)
            BulkAudioState.Extracting -> notify(1f)
            is BulkAudioState.Downloaded,
            is BulkAudioState.Failed -> stopSelf()
            else -> Unit
        }
    }

    private fun notify(progress: Float) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(progress))
    }

    private fun buildNotification(progress: Float): Notification {
        val percent = (progress * 100).toInt()
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading Gita audio")
            .setContentText("$percent%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, percent, false)
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Audio download",
                    NotificationManager.IMPORTANCE_LOW,
                )
            )
        }
    }

    private companion object {
        const val CHANNEL_ID = "audio_download"
        const val NOTIFICATION_ID = 4201
    }
}
