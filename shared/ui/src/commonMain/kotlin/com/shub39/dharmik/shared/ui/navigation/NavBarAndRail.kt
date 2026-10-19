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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dharmik.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private data class NavItem(val route: Route, val title: StringResource, val icon: DrawableResource)

private val NAV_ITEMS =
    listOf(
        NavItem(Home, Res.string.home, Res.drawable.home),
        NavItem(Chapters, Res.string.chapters, Res.drawable.book),
    )

@Composable
fun NavBar(modifier: Modifier = Modifier, currentRoute: Route?, onNavigate: (Route) -> Unit) {
    if (currentRoute !in baseRoutes) return

    NavigationBar(modifier = modifier) {
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) return@NavigationBarItem
                    onNavigate(item.route)
                },
                icon = { Icon(imageVector = vectorResource(item.icon), contentDescription = null) },
                label = { Text(text = stringResource(item.title)) },
                alwaysShowLabel = false,
            )
        }
    }
}

@Composable
fun NavRail(modifier: Modifier = Modifier, currentRoute: Route?, onNavigate: (Route) -> Unit) {
    if (currentRoute !in baseRoutes) return

    NavigationRail(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute == item.route

            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (selected) return@NavigationRailItem
                    onNavigate(item.route)
                },
                icon = { Icon(imageVector = vectorResource(item.icon), contentDescription = null) },
                label = { Text(text = stringResource(item.title)) },
                alwaysShowLabel = false,
            )
        }
    }
}
