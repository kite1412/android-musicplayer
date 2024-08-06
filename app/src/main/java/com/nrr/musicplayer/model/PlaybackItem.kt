package com.nrr.musicplayer.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

data class PlaybackItem(
    val playbackProgress: MutableState<Float> = mutableFloatStateOf(0f),
    val data: MutableState<FormattedAudioFile> = mutableStateOf(FormattedAudioFile()),
)

fun PlaybackItem.playing(): Boolean = !data.value.noData()