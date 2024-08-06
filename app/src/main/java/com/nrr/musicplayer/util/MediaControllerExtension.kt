package com.nrr.musicplayer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.media3.session.MediaController
import com.nrr.musicplayer.LocalMediaController
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.model.toMediaItems

val ProvidableCompositionLocal<MediaController?>.currentOrThrow
    @Composable get() = LocalMediaController.current!!

fun MediaController.playMedia(startIndex: Int, playlist: List<FormattedAudioFile>) {
    setMediaItems(playlist.toMediaItems(), startIndex, 0)
    prepare()
    play()
}