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
package com.shub39.dharmik

// import android.Manifest // bulk audio download notification (commented out for now)
// import android.content.pm.PackageManager // bulk audio download notification (commented out for
// now)
// import android.os.Build // bulk audio download notification (commented out for now)
// import androidx.activity.result.contract.ActivityResultContracts // bulk audio download
// notification (commented out for now)
// import androidx.core.content.ContextCompat // bulk audio download notification (commented out for
// now)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.dharmik.shared.ui.navigation.DharmikNavDisplay

class MainActivity : ComponentActivity() {

    // Bulk audio download notification permission (commented out for now)
    // private val notificationPermission =
    //     registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // requestNotificationPermissionIfNeeded() // bulk audio download notification (commented
        // out for now)

        setContent {
            enableEdgeToEdge()
            DharmikNavDisplay()
        }
    }

    /*
     * The bulk audio download runs in a foreground service that needs a visible notification.
     * Notification permission prompt commented out for now along with the download UI.
     *
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (!granted) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
     */
}
