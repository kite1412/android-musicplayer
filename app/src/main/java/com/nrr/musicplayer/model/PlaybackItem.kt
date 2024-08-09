package com.nrr.musicplayer.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

data class PlaybackItem(
    val playbackProgress: MutableState<Float> = mutableFloatStateOf(0f),
    val data: MutableState<FormattedAudioFile> = mutableStateOf(FormattedAudioFile()),
    val isPlaying: MutableState<Boolean> = mutableStateOf(false),
    var index: Int = 0
)

fun PlaybackItem.available(): State<Boolean> = derivedStateOf { !data.value.noData() }

fun PlaybackItem.clear(): Unit {
    playbackProgress.value = 0f
    data.value = FormattedAudioFile()
    isPlaying.value = false
}