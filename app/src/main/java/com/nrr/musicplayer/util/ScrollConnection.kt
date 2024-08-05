package com.nrr.musicplayer.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

class ScrollConnection(
    private val consume: Boolean,
    private val onScroll: (delta: Float) -> Unit
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        if (consume) available.also { onScroll(it.y) } else Offset.Zero
}