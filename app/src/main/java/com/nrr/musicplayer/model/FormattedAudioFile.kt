package com.nrr.musicplayer.model

import android.os.Parcelable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class FormattedAudioFile(
    val displayName: String = "",
    val durationDisplay: String = "",
    val duration: Int = 0,
    val raw: AudioFile = AudioFile()
) : Parcelable {
    companion object {
        private fun from(raw: AudioFile): FormattedAudioFile {
            val seconds = TimeUnit.MILLISECONDS.toSeconds(raw.duration.toLong())
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return FormattedAudioFile(
                displayName = raw.displayName.replace(".mp3", ""),
                durationDisplay = String.format(null, "%d:%02d", minutes, remainingSeconds),
                duration = seconds.toInt(),
                raw = raw
            )
        }

        fun from(rawAudioFiles: List<AudioFile>): List<FormattedAudioFile> =
            rawAudioFiles.map { from(it) }
    }
}

fun FormattedAudioFile.noData(): Boolean = this == FormattedAudioFile()

fun FormattedAudioFile.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(raw.data)
    .setUri(raw.data)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(displayName)
            .setAlbumTitle(raw.album)
            .build()
    )
    .build()

fun List<FormattedAudioFile>.toMediaItems(): List<MediaItem> = map { it.toMediaItem() }