package com.nrr.musicplayer.model

import androidx.annotation.OptIn
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.nrr.musicplayer.util.msToFloatProgress

data class PlaybackItem(
    val playbackProgress: MutableState<Float> = mutableFloatStateOf(0f),
    val data: MutableState<FormattedAudioFile> = mutableStateOf(FormattedAudioFile()),
    val isPlaying: MutableState<Boolean> = mutableStateOf(false),
    var index: MutableIntState = mutableIntStateOf(0)
) {
    companion object {
        @OptIn(UnstableApi::class)
        fun create(mediaController: MediaController?): PlaybackItem =
            PlaybackItem(
                data = mutableStateOf(FormattedAudioFile.from(mediaController?.currentMediaItem)),
                playbackProgress = mutableFloatStateOf(
                    msToFloatProgress(
                        ms = mediaController?.currentPosition?.toInt() ?: 0,
                        duration = mediaController?.mediaMetadata?.durationMs?.toInt() ?: 0
                    )
                ),
                isPlaying = mutableStateOf(mediaController?.isPlaying ?: false),
                index = mutableIntStateOf(mediaController?.currentMediaItemIndex ?: 0)
            )
    }
}

fun PlaybackItem.available(): State<Boolean> = derivedStateOf { !data.value.noData() }

fun PlaybackItem.clear(): Unit {
    playbackProgress.value = 0f
    data.value = FormattedAudioFile()
    isPlaying.value = false
}