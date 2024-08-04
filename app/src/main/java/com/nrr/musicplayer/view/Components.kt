package com.nrr.musicplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.musicplayer.R

@Composable
fun MusicNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
    size: Dp = 48.dp,
    backgroundColor: Color = Color.White,
    clipSize: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(clipSize))
            .background(backgroundColor)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.music_note),
            contentDescription = "music note",
            tint = tint,
            modifier = Modifier
                .size(size / 2)
                .align(Alignment.Center)
        )
    }
}