package com.nrr.musicplayer.model

import android.os.Parcelable
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.nrr.musicplayer.util.msToSec
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
            val seconds = msToSec(raw.duration)
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return FormattedAudioFile(
                displayName = raw.displayName.replace(".mp3", ""),
                durationDisplay = String.format(null, "%d:%02d", minutes, remainingSeconds),
                duration = seconds,
                raw = raw
            )
        }

        fun from(rawAudioFiles: List<AudioFile>): List<FormattedAudioFile> =
            rawAudioFiles.map { from(it) }

        @OptIn(UnstableApi::class)
        fun from(mediaItem: MediaItem?): FormattedAudioFile {
            return FormattedAudioFile(
                displayName = mediaItem?.mediaMetadata?.title?.toString() ?: "",
                duration = msToSec(mediaItem?.mediaMetadata?.durationMs?.toInt() ?: 0),
                raw = mediaItem.toAudioFile()
            )
        }
    }
}

fun FormattedAudioFile.noData(): Boolean = this == FormattedAudioFile()

@OptIn(UnstableApi::class)
fun FormattedAudioFile.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(raw.data)
    .setUri(raw.data)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(displayName)
            .setAlbumTitle(raw.album)
            .setDurationMs(TimeUnit.SECONDS.toMillis(duration.toLong()))
            .build()
    )
    .build()

fun List<FormattedAudioFile>.toMediaItems(): List<MediaItem> = map { it.toMediaItem() }