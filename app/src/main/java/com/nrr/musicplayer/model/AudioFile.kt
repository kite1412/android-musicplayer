package com.nrr.musicplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val displayName: String = "",
    val duration: Int = 0,
    val album: String = "",
    val data: String = ""
) : Parcelable
