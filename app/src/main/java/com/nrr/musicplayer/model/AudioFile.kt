package com.nrr.musicplayer.model

import android.os.Parcelable
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val displayName: String = "",
    val duration: Int = 0,
    val album: String = "",
    val data: String = ""
) : Parcelable

@OptIn(UnstableApi::class)
fun MediaItem?.toAudioFile(): AudioFile = AudioFile(
    displayName = this?.mediaMetadata?.title.toString(),
    duration = this?.mediaMetadata?.durationMs?.toInt() ?: 0,
    album = this?.mediaMetadata?.albumTitle.toString(),
    data = this?.localConfiguration?.uri?.toString() ?: ""
)
