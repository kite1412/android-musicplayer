package com.nrr.musicplayer.data_store

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object DataStoreKeys {
    val REPEAT_STATE = intPreferencesKey("repeat_state")
    val SHUFFLE = booleanPreferencesKey("shuffle")
}