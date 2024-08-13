package com.nrr.musicplayer.media

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.model.PlaybackItem
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.util.msToFloatProgress
import com.nrr.musicplayer.util.playMedia
import com.nrr.musicplayer.util.progressToMs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Player(
    private val mediaController: MediaController?,
    initialPlaybackItem: PlaybackItem = PlaybackItem(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Player.Listener {
    val playbackItem = initialPlaybackItem
    private var files = listOf<FormattedAudioFile>()
    private var prevProgress = 0f
    private var listenCurrentPosition = true

    fun listen() {
        mediaController?.addListener(this)
        listenCurrentPosition()
    }

    private fun listenCurrentPosition() {
        scope.launch {
            while (true) {
                val currentPosition = mediaController!!.currentPosition
                if (currentPosition != 0L && listenCurrentPosition) {
                    val progress = msToFloatProgress(currentPosition.toInt(), playbackItem.data.value.duration)
                    playbackItem.playbackProgress.value = progress
                    prevProgress = progress
                    Log.d(progress.toString())
                }
                Log.d("listenCurrentPosition")
                delay(1000)
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        playbackItem.data.value = FormattedAudioFile.from(mediaItem)
        Log.d("onMediaItemTransition: $reason")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        playbackItem.isPlaying.value = isPlaying
    }

    fun play(
        startIndex: Int,
        files: List<FormattedAudioFile>,
        startPosition: Long = 0L
    ) {
        val file = files[startIndex]
        playbackItem.data.value = file
        playbackItem.isPlaying.value = true
        playbackItem.playbackProgress.value = msToFloatProgress(startPosition.toInt(), file.duration)
        playbackItem.index = startIndex
        this.files = files
        mediaController?.playMedia(startIndex, files, startPosition)
    }

    private fun pause() {
        playbackItem.isPlaying.value = false
        mediaController?.pause()
    }

    fun playPause(
        play: Boolean
    ) {
        if (play) play(
            playbackItem.index,
            files,
            mediaController?.currentPosition ?: 0L
        ) else pause()
    }

    fun clear() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
    }

    fun restart() {
        scope.cancel()
    }

    fun setTemporaryProgress(progress: Float) {
        listenCurrentPosition = false
        playbackItem.playbackProgress.value = progress
    }

    fun seek(progress: Float) {
        mediaController?.seekTo(progressToMs(
            progress = progress,
            duration = playbackItem.data.value.raw.duration
        ).toLong())
        listenCurrentPosition = true
    }
}