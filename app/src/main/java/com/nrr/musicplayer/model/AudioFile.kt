package com.nrr.musicplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val displayName: String,
    val duration: Int,
    val album: String,
    val data: String
) : Parcelable
