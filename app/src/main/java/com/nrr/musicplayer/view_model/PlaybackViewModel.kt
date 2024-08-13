package com.nrr.musicplayer.view_model

import androidx.lifecycle.ViewModel
import com.nrr.musicplayer.media.Player

class PlaybackViewModel : ViewModel() {
    fun onProgressChange(progress: Float, player: Player) {
        player.setTemporaryProgress(progress)
    }

    fun onProgressChangeFinished(player: Player) {
        player.seek(player.playbackItem.playbackProgress.value)
    }
}