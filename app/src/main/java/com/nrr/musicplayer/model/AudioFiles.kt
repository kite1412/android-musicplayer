package com.nrr.musicplayer.model

data class AudioFiles(
    val audioFiles: List<AudioFile> = emptyList(),
    val error: Throwable? = null
)
