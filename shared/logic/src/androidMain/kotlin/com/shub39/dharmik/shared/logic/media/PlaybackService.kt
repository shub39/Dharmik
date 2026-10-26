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
package com.shub39.dharmik.shared.logic.media

import android.app.PendingIntent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * Hosts the app's only [ExoPlayer] and its [MediaSession] in a foreground service, so playback
 * keeps running while the app is in the background.
 *
 * Because the whole playlist lives in the player, media3 publishes the `MediaStyle` notification
 * and exposes the playlist navigation (previous/next) to the notification, the lock screen,
 * Bluetooth devices and Assistant without any custom notification code.
 *
 * The service is started by connecting a `MediaController` to it (see [MediaSessionClient]) and is
 * stopped by the system once playback has been idle for a while.
 */
@UnstableApi
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    /* handleAudioFocus = */ true,
                )
                .setHandleAudioBecomingNoisy(true)
                .build()

        mediaSession = MediaSession.Builder(this, player).setSessionActivityIfAvailable().build()
    }

    private fun MediaSession.Builder.setSessionActivityIfAvailable(): MediaSession.Builder {
        val activity = sessionActivity() ?: return this
        return setSessionActivity(activity)
    }

    /** Opens the app when the notification is tapped. */
    private fun sessionActivity(): PendingIntent? =
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
