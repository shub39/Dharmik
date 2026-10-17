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

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

sealed interface Route : NavKey

/**
 * The reading screen.
 *
 * The verse that should be shown travels on the route itself, so that no shared in-memory state is
 * needed to hand a selection over from another screen.
 *
 * @param chapter the chapter to display.
 * @param verse zero based index of the verse to page to.
 * @param saveBookmarks whether paging between verses should also update the stored bookmark.
 */
data class Verses(val chapter: Long, val verse: Long = 0, val saveBookmarks: Boolean = true) : Route

data object Home : Route

data object Chapters : Route

data object Settings : Route

val baseRoutes = listOf(Home, Chapters)

class TopLevelBackStack(private val startKey: Route = Home) {
    val backStack = mutableStateListOf(startKey)

    fun isRouteOnTop(route: Route) = backStack.lastOrNull() == route

    fun add(route: Route) {
        if (isRouteOnTop(route)) return

        when (route) {
            Home -> {
                backStack.clear()
                backStack.add(Home)
            }
            Settings -> {
                backStack.clear()
                backStack.add(Home)
                backStack.add(Settings)
            }
            Chapters -> {
                backStack.clear()
                backStack.add(Home)
                backStack.add(Chapters)
            }
            is Verses -> {
                backStack.removeAll { it is Verses }
                backStack.add(route)
            }
        }
    }

    fun removeLast() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
}
