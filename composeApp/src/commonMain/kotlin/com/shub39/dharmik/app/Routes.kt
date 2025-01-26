package com.shub39.dharmik.app

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Home: Routes
    @Serializable
    data object SavedPage: Routes
    @Serializable
    data object LikedPage: Routes
    @Serializable
    data object LibraryPage: Routes
    @Serializable
    data object SettingsPage: Routes

    @Serializable
    data object AtharvaVedaGraph: Routes

}