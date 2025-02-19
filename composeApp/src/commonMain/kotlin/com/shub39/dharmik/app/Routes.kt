package com.shub39.dharmik.app

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Home: Routes
    @Serializable
    data object HomeSection: Routes
    @Serializable
    data object LibrarySection: Routes
    @Serializable
    data object SettingsSection: Routes

    @Serializable
    data object AtharvaVedaGraph: Routes
    @Serializable
    data object AvKaandasPage: Routes
    @Serializable
    data object AvVersesPage: Routes
    @Serializable
    data object AvFavVersesPage: Routes

    @Serializable
    data object BhagvadGitaGraph: Routes
    @Serializable
    data object BgChaptersPage: Routes
    @Serializable
    data object BgChapterVersesPage: Routes
    @Serializable
    data object BgFavVersesPage: Routes

}