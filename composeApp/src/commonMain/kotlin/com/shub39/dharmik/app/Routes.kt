package com.shub39.dharmik.app

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Home: Routes
    @Serializable
    data object BhagvadGitaGraph: Routes
    @Serializable
    data object BgChaptersPage: Routes
    @Serializable
    data object BgChapterVersesPage: Routes
    @Serializable
    data object BgFavVersesPage: Routes

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