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
 * @param verse zero based index of the verse to page to, i.e. the pager page. Screens that know a
 *   verse by its one based number (collections, favourites) must build the route with [versesAt].
 * @param saveBookmarks whether paging between verses should also update the stored bookmark.
 */
data class Verses(val chapter: Long, val verse: Long = 0, val saveBookmarks: Boolean = true) : Route

/**
 * Opens [chapter] at [verseNumber], a one based verse number, which is how collections, favourites
 * and the verse files themselves number verses.
 */
fun versesAt(chapter: Long, verseNumber: Long, saveBookmarks: Boolean = false): Verses =
    Verses(
        chapter = chapter,
        verse = (verseNumber - 1).coerceAtLeast(0),
        saveBookmarks = saveBookmarks,
    )

/**
 * The collection screen.
 *
 * @param id the collection to display.
 */
data class CollectionDetail(val id: Long) : Route

/**
 * The "add this verse to a collection" screen.
 *
 * @param chapter the chapter of the verse being collected.
 * @param verse the one based verse number being collected.
 */
data class AddToCollection(val chapter: Long, val verse: Long) : Route

/**
 * The audio player.
 *
 * The queue a new session should start with travels on the route, like the verse of [Verses], so no
 * shared mutable state is needed to hand a selection over from another screen. Both parameters left
 * `null` opens whatever is currently playing.
 *
 * @param chapter start playing the whole chapter.
 * @param collectionId start playing the verses of a collection.
 */
data class Player(val chapter: Long? = null, val collectionId: Long? = null) : Route

data object Home : Route

data object Chapters : Route

data object Collections : Route

data object Settings : Route

data object About : Route

data object Changelog : Route

data object Paywall : Route

val baseRoutes = listOf(Home, Chapters, Collections, Player())

/**
 * Whether this route is one of the navigation destinations, i.e. whether the nav bar/rail belongs
 * on screen. [Player] is a destination for every one of its states, so it cannot be compared by
 * value.
 */
val Route.isBaseRoute: Boolean
    get() =
        when (this) {
            Home,
            Chapters,
            Collections,
            is Player -> true
            else -> false
        }

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
            Collections -> {
                backStack.clear()
                backStack.add(Home)
                backStack.add(Collections)
            }
            is Player -> {
                backStack.clear()
                backStack.add(Home)
                backStack.add(route)
            }
            is Verses -> {
                backStack.removeAll { it is Verses }
                backStack.add(route)
            }
            is CollectionDetail -> {
                backStack.removeAll { it is CollectionDetail }
                backStack.add(route)
            }
            is AddToCollection -> {
                backStack.removeAll { it is AddToCollection }
                backStack.add(route)
            }
            About,
            Changelog,
            Paywall -> {
                backStack.add(route)
            }
        }
    }

    fun removeLast() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
}
