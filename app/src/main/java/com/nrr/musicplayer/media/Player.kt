package com.nrr.musicplayer.media

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.media3.common.MediaItem
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
import androidx.media3.common.Player as player

class Player private constructor (
    private val mediaController: MediaController?,
    val playbackItem: PlaybackItem = PlaybackItem(),
    var files: SnapshotStateList<FormattedAudioFile> = mutableStateListOf(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : player.Listener {
    private var prevProgress = 0f
    private var listenCurrentPosition = true
    private var inTransition = false

    companion object {
        private fun resolveFiles(mediaController: MediaController?): List<FormattedAudioFile> {
            val mediaItems = mutableListOf<MediaItem?>()
            var index = 0
            while (index < (mediaController?.mediaItemCount ?: 0)) {
                mediaItems.add(mediaController?.getMediaItemAt(index))
                index++
            }
            return mediaItems.map { FormattedAudioFile.from(it) }
        }

        fun create(mediaController: MediaController?): Player = Player(
            mediaController = mediaController,
            playbackItem = PlaybackItem.create(mediaController),
            files = resolveFiles(mediaController).toMutableStateList()
        )

        fun create(mediaController: MediaController?, files: SnapshotStateList<FormattedAudioFile>): Player = Player(
            mediaController = mediaController,
            playbackItem = PlaybackItem.create(mediaController),
            files = files
        )
    }

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
        playbackItem.data.value = FormattedAudioFile.from(mediaItem).also {
            Log.d("playing: ${it.displayName}")
        }
        playbackItem.index.intValue = mediaController?.currentMediaItemIndex ?: 0
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (inTransition) inTransition = false
        else playbackItem.isPlaying.value = isPlaying
    }

    fun play(
        startIndex: Int,
        files: List<FormattedAudioFile>,
        startPosition: Long = 0L
    ) {
        if (files.isEmpty()) return
        val file = files[startIndex]
        playbackItem.data.value = file
        if (!playbackItem.isPlaying.value) playbackItem.isPlaying.value = true
        playbackItem.playbackProgress.value = msToFloatProgress(startPosition.toInt(), file.duration)
        playbackItem.index.intValue = startIndex
        this.files = files.toMutableStateList()
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
            playbackItem.index.intValue,
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
        inTransition = true
        mediaController?.seekTo(progressToMs(
            progress = progress,
            durationMs = playbackItem.data.value.raw.duration
        ).toLong())
        listenCurrentPosition = true
    }

    fun hasNext(): Boolean = mediaController?.hasNextMediaItem() ?: false

    fun hasPrevious(): Boolean = mediaController?.hasPreviousMediaItem() ?: false

    fun next() {
        playbackItem.playbackProgress.value = 0f
        inTransition = true
        mediaController?.seekToNextMediaItem()
    }

    fun previous() {
        playbackItem.playbackProgress.value = 0f
        inTransition = true
        mediaController?.seekToPreviousMediaItem()
    }
}