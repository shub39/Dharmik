package com.shub39.dharmik.app

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Home: Routes
    @Serializable
    data object Verses: Routes
    @Serializable
    data object FavVerses: Routes
}

sealed interface HomeRoutes {
    @Serializable
    data object HomeSection: HomeRoutes
    @Serializable
    data object LibrarySection: HomeRoutes
    @Serializable
    data object SettingsSection: HomeRoutes
    @Serializable
    data object RemindersSection: HomeRoutes
}