package com.nrr.musicplayer.util

import android.os.Build

inline fun <reified T> minApiLevel(
    minApiLevel: Int,
    onApiLevelRange: () -> T,
    onApiLevelBelow: () -> T,
): T = if (Build.VERSION.SDK_INT >= minApiLevel) onApiLevelRange()
        else onApiLevelBelow()