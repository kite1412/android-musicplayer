package com.nrr.musicplayer.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val menus = listOf("Songs", "Playlists")
    var currentMenu by mutableStateOf(menus[0])
    var animate by mutableStateOf(false)
    var executeOnce by mutableStateOf(false)
    var playing by mutableStateOf(false)
    var closePlayBar by mutableStateOf(false)

    init {
        viewModelScope.launch {
            delay(200)
            animate = true
        }
    }
}