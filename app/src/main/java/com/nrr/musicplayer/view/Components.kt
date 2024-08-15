package com.nrr.musicplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.musicplayer.LocalPlayer
import com.nrr.musicplayer.R
import com.nrr.musicplayer.model.FormattedAudioFile
import com.nrr.musicplayer.ui.theme.SoftSilver
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
    val state = remember {
        LazyListState()
    }
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

@Composable
fun Songs(
    files: List<FormattedAudioFile>,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp
    )
) {
    val player = LocalPlayer.current
    if (files.isNotEmpty()) LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(files.size) {
            Song(
                file = files[it],
                modifier = Modifier
                    .background(
                        if (showIndicator && player.playbackItem.index.intValue == it) MaterialTheme.colorScheme.tertiary
                            else Color.Transparent
                    )
                    .clickable {
                        player.play(it, files)
                    }
                    .padding(8.dp)
            )
        }
    } else Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val config = LocalConfiguration.current
        Icon(
            painter = painterResource(id = R.drawable.box_open),
            contentDescription = "empty",
            modifier = Modifier.size(config.screenWidthDp.dp / 3),
            tint = if (isSystemInDarkTheme()) SoftSilver else WarmCharcoal
        )
        Text(
            text = "No songs found",
            fontSize = 20.sp
        )
    }
}

@Composable
private fun Song(
    file: FormattedAudioFile,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MusicNoteIcon()
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = file.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = file.durationDisplay,
                fontSize = 12.sp
            )
        }
    }
}