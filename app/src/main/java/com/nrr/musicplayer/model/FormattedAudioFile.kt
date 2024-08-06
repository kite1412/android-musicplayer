package com.nrr.musicplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class FormattedAudioFile(
    val displayName: String,
    val duration: String,
    val raw: AudioFile
) : Parcelable {
    companion object {
        private fun from(raw: AudioFile): FormattedAudioFile = FormattedAudioFile(
            displayName = raw.displayName.replace(".mp3", ""),
            duration = run {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(raw.duration.toLong())
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                String.format(null, "%d:%02d", minutes, remainingSeconds)
            },
            raw = raw
        )

        fun from(rawAudioFiles: List<AudioFile>): List<FormattedAudioFile> =
            rawAudioFiles.map { from(it) }
    }
}
