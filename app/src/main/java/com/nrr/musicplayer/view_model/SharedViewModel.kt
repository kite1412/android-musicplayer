package com.nrr.musicplayer.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.nrr.musicplayer.model.FormattedAudioFile

class SharedViewModel : ViewModel() {
    var audioFiles = mutableStateListOf<FormattedAudioFile>()
    private var executeOnce: Boolean = false

    fun loadAudioFiles(files: List<FormattedAudioFile>) {
        if (!executeOnce) {
            audioFiles.addAll(files)
            executeOnce = true
        }
    }
}