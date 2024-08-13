package com.nrr.musicplayer.util

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nrr.musicplayer.view_model.SharedViewModel
import java.util.concurrent.TimeUnit

inline fun <reified T> minApiLevel(
    minApiLevel: Int,
    onApiLevelRange: () -> T,
    onApiLevelBelow: () -> T,
): T = if (Build.VERSION.SDK_INT >= minApiLevel) onApiLevelRange()
        else onApiLevelBelow()

@Composable
fun sharedViewModel(): SharedViewModel = viewModel(
    modelClass = SharedViewModel::class,
    viewModelStoreOwner = LocalContext.current as ComponentActivity
)

fun msToSec(ms: Int): Int = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()).toInt()

fun msToFloatProgress(ms: Int, duration: Int): Float = msToSec(ms) / duration.toFloat()

fun progressToMs(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    duration: Int
): Int = (progress * duration).toInt()