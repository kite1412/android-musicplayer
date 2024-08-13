package com.nrr.musicplayer.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nrr.musicplayer.LocalPlayer
import com.nrr.musicplayer.R
import com.nrr.musicplayer.ui.theme.SoftSilver
import com.nrr.musicplayer.ui.theme.WarmCharcoal
import com.nrr.musicplayer.view_model.PlaybackViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playback(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    vm: PlaybackViewModel = viewModel(PlaybackViewModel::class)
) {
    val player = LocalPlayer.current
    VerticalPager(
        state = rememberPagerState { 2 },
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars),
        userScrollEnabled = false
    ) {
        when (it) {
            0 -> PlaybackControl(
                sliderProgress = player.playbackItem.playbackProgress.value,
                onProgressChange = { p -> vm.onProgressChange(p, player) },
                onProgressChangeFinished = { vm.onProgressChangeFinished(player) }
            ) {
                navHostController.popBackStack()
            }
            1 -> Text(text = "Empty")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(text = "Now Playing")
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "back"
                )
            }
        },
    )
}

@Composable
private fun PlaybackControl(
    sliderProgress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onProgressChangeFinished: (() -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val player = LocalPlayer.current.playbackItem
            Header(onNavigateBack = onNavigateBack)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MusicNoteIcon(
                    size = this@BoxWithConstraints.maxWidth - 32.dp,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    clipSize = 16.dp
                )
                MusicTitle(
                    title = player.data.value.displayName,
                    onLike = {},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp)
                )
                ProgressSlider(
                    value = sliderProgress,
                    onValueChange = onProgressChange,
                    onValueChangeFinished = onProgressChangeFinished,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun MusicTitle(
    title: String,
    onLike: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxWidth()) {
        SlidingText(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            style = LocalTextStyle.current.copy(
                fontSize = 20.sp,
            )
        )
        Icon(
            painter = painterResource(id = R.drawable.heart_outlined),
            contentDescription = "like / undo",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.dp)
                .background(MaterialTheme.colorScheme.background)
                .clip(CircleShape)
                .clickable { },
            tint = Color(0xFFF30000)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null
) {
    val activeColor = if (isSystemInDarkTheme()) SoftSilver else WarmCharcoal
    val inactiveColor = if (isSystemInDarkTheme()) WarmCharcoal else SoftSilver
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = activeColor,
            activeTrackColor = activeColor,
            inactiveTrackColor = inactiveColor
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(activeColor)
            )
        }
    )
}