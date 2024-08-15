package com.nrr.musicplayer.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.nrr.musicplayer.model.FormattedAudioFile

class SharedViewModel : ViewModel() {
    var audioFiles = mutableStateListOf<FormattedAudioFile>()

    fun addAudioFiles(files: List<FormattedAudioFile>) {
        audioFiles.addAll(files)
    }
}