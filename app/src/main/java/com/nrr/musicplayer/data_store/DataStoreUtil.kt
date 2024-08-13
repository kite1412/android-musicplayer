package com.nrr.musicplayer.data_store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nrr.musicplayer.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun dataStore(): DataStore<Preferences> = LocalContext.current.dataStore

@Composable
fun collectString(key: Preferences.Key<String>): State<String> = dataStore().data
    .map {
            it[key] ?: ""
        }.collectAsState(initial = "")

@Composable
fun collectInt(key: Preferences.Key<Int>): State<Int> = dataStore().data
    .map {
        it[key] ?: 0
    }.collectAsState(initial = 0)