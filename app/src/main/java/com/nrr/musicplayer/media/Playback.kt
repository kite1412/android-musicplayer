package com.nrr.musicplayer.media

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.model.PlaybackItem
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.util.playMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class Playback(
    private val mediaController: MediaController?,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Player.Listener {
    val playbackItem = PlaybackItem()

    init {
        listenCurrentPosition()
    }

    private fun listenCurrentPosition() {
        scope.launch {
            while (true) {
                if (mediaController != null) {
                    val currentPosition = mediaController.currentPosition
                    if (currentPosition != 0L) {
                        playbackItem.playbackProgress.value =
                            TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) / playbackItem.data.value.duration.toFloat()
                        Log.d(playbackItem.playbackProgress.value.toString())
                    }
                }
                delay(1000)
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        playbackItem.data.value = FormattedAudioFile.from(mediaItem)
    }

    fun play(startIndex: Int, files: List<FormattedAudioFile>) {
        playbackItem.data.value = files[startIndex]
        mediaController?.playMedia(startIndex, files)
    }

    fun restart() {
        scope.cancel()
    }
}