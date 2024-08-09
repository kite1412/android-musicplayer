package com.nrr.musicplayer.util

import androidx.media3.session.MediaController
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.model.toMediaItems

fun MediaController.playMedia(
    startIndex: Int,
    playlist: List<FormattedAudioFile>,
    startPosition: Long = 0L
) {
    setMediaItems(playlist.toMediaItems(), startIndex, startPosition)
    prepare()
    play()
}