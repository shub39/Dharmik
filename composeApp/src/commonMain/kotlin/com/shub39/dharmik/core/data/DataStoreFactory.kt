package com.shub39.dharmik.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

expect class DataStoreFactory {
    fun getPreferencesDataStore(): DataStore<Preferences>
}

internal const val preferencesFileName = "dharmik.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })