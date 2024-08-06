package com.nrr.musicplayer.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.model.PlaybackItem
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.util.playMedia
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SharedViewModel : ViewModel() {
    var audioFiles = mutableStateListOf<FormattedAudioFile>()
    var currentlyPlaying = PlaybackItem()

    // TODO change position reading strategy
    fun play(controller: MediaController?, startIndex: Int, files: List<FormattedAudioFile>) {
        currentlyPlaying.data.value = files[startIndex]
        controller?.playMedia(startIndex, files)
        viewModelScope.launch {
            while(true) {
                Log.d(controller?.currentPosition.toString())
                val currentPosition = controller?.currentPosition ?: 0
                val second = TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                currentlyPlaying.playbackProgress.value = second / currentlyPlaying.data.value.duration.toFloat()
                delay(1000)
            }
        }
    }
}