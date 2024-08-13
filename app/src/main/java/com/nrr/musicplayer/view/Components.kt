package com.nrr.musicplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.musicplayer.R
import com.nrr.musicplayer.ui.theme.WarmCharcoal
import kotlinx.coroutines.delay

@Composable
fun MusicNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = if (!isSystemInDarkTheme()) Color.Black else Color.White,
    size: Dp = 48.dp,
    backgroundColor: Color = if (!isSystemInDarkTheme()) Color.White else WarmCharcoal,
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

@Composable
fun SlidingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    var isOverflow by remember(text) {
        mutableStateOf(false)
    }
    var pause by remember {
        mutableStateOf(false)
    }
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }
    var textLength by remember {
        mutableFloatStateOf(0f)
    }
    val state = rememberLazyListState()
    val density = LocalDensity.current
    LaunchedEffect(isOverflow, pause) {
        delay(2000)
        while (isOverflow && !pause) {
            if (offsetX < textLength) {
                delay(15)
                with(density) {
                    1.dp.toPx().also {
                        state.scrollBy(it)
                        offsetX += it
                    }
                }
            } else {
                delay(100)
                state.scrollBy(-offsetX)
                offsetX = 0f
                pause = true
                break
            }
        }
    }
    LaunchedEffect(pause) {
        if (pause) {
            pause = false
        }
    }
    LaunchedEffect(text) {
        state.scrollBy(-offsetX)
        offsetX = 0f
    }
    BoxWithConstraints(modifier) {
        val space = maxWidth / 2
        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(space),
            userScrollEnabled = false
        ) {
            item {
                Text(
                    text = text,
                    maxLines = 1,
                    style = style,
                    onTextLayout = {
                        with(density) {
                            val width = it.size.width
                            if (width >= this@BoxWithConstraints.maxWidth.toPx()) {
                                isOverflow = true
                                textLength = width + space.toPx()
                            }
                        }
                    }
                )
            }
            if (isOverflow) item {
                Text(
                    text = text,
                    maxLines = 1,
                    style = style
                )
            }
        }
    }
}