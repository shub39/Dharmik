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
package com.shub39.dharmik.shared.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/** Top level destinations of the app, backed by a Navigation 3 back stack. */
@Serializable
sealed interface Routes : NavKey {
    @Serializable data object Home : Routes

    @Serializable data object Verses : Routes

    companion object {
        /**
         * Serializer configuration used to remember the back stack across process death. Required
         * on non-Android targets, where the reflection based overload is unavailable.
         */
        val configuration: SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Home::class, Home.serializer())
                    subclass(Verses::class, Verses.serializer())
                }
            }
        }
    }
}

/** Sections shown inside the [Routes.Home] navigation suite. */
sealed interface HomeRoutes {
    @Serializable data object HomeSection : HomeRoutes

    @Serializable data object ChaptersSection : HomeRoutes

    @Serializable data object SettingsSection : HomeRoutes
}
