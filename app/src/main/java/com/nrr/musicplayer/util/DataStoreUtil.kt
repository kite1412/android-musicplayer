package com.nrr.musicplayer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nrr.musicplayer.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun dataStore(): DataStore<Preferences> = LocalContext.current.dataStore

@Composable
fun collectString(key: String): State<String> = dataStore().data.
        map {
            it[stringPreferencesKey("key")] ?: ""
        }.collectAsState(initial = "")
