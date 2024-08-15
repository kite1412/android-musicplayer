package com.nrr.musicplayer.view_model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.musicplayer.dataStore
import com.nrr.musicplayer.data_store.DataStoreKeys
import com.nrr.musicplayer.media.Player
import com.nrr.musicplayer.util.RepeatState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaybackViewModel(
    context: Context
) : ViewModel() {
    var repeatState by mutableStateOf(RepeatState.ON)
    var shuffle by mutableStateOf(false)

    init {
        viewModelScope.launch {
            context.dataStore.data.map {
                it[DataStoreKeys.REPEAT_STATE]?.let { s ->
                    repeatState = RepeatState.from(s)
                }
                it[DataStoreKeys.SHUFFLE]?.let { s ->
                    shuffle = s
                }
            }.first()
        }
    }

    fun onRepeatStateChange(context: Context) {
        repeatState = RepeatState.next(repeatState)
        viewModelScope.launch {
            context.dataStore.edit {
                it[DataStoreKeys.REPEAT_STATE] = repeatState.ordinal
            }
        }
    }

    fun onShuffleChange(context: Context) {
        shuffle = !shuffle
        viewModelScope.launch {
            context.dataStore.edit {
                it[DataStoreKeys.SHUFFLE] = shuffle
            }
        }
    }

    fun onProgressChange(progress: Float, player: Player) {
        player.setTemporaryProgress(progress)
    }

    fun onProgressChangeFinished(player: Player) {
        player.seek(player.playbackItem.playbackProgress.value)
    }
}