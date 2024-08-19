package com.nrr.musicplayer.view

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nrr.musicplayer.LocalPlayer
import com.nrr.musicplayer.R
import com.nrr.musicplayer.media.Player
import com.nrr.musicplayer.ui.theme.DeeperCharcoal
import com.nrr.musicplayer.ui.theme.SoftSilver
import com.nrr.musicplayer.ui.theme.WarmCharcoal
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.util.RepeatState
import com.nrr.musicplayer.util.ScrollConnection
import com.nrr.musicplayer.view_model.PlaybackViewModel
import kotlinx.coroutines.launch

@Composable
private fun playbackViewModel(): PlaybackViewModel = LocalContext.current.run {
    viewModel { PlaybackViewModel(this@run) }
}

private fun playlistBackgroundColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DeeperCharcoal else Color(0xFFE9E9E9)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playback(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    vm: PlaybackViewModel = playbackViewModel()
) {
    val player = LocalPlayer.current
    val context = LocalContext.current
    val pagerState = rememberPagerState { 2 }
    val currentPage by remember {
        derivedStateOf { pagerState.currentPage }
    }
    val isDarkMode = isSystemInDarkTheme()
    val playbackControlClip by animateDpAsState(
        targetValue = if (currentPage == 1) 16.dp else 0.dp,
        label = "playback_control_clip"
    )
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    LaunchedEffect(pagerState.settledPage) {
        state.scrollToItem(player.playbackItem.index.intValue)
    }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        val pageSize by remember {
            derivedStateOf {
                if (currentPage == 0) maxHeight
                else maxHeight * 0.9f
            }
        }
        VerticalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(pageSize),
            userScrollEnabled = currentPage == 0,
            pageNestedScrollConnection = ScrollConnection(false) {}
        ) {
            when (it) {
                0 -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(playlistBackgroundColor(isDarkMode))
                        .clip(
                            RoundedCornerShape(
                                bottomStart = playbackControlClip,
                                bottomEnd = playbackControlClip
                            )
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .shadow(
                            elevation = if (currentPage == 1) 2.dp else 0.dp,
                            shape = RoundedCornerShape(
                                bottomStart = playbackControlClip,
                                bottomEnd = playbackControlClip
                            ),
                            spotColor = if (isDarkMode) Color.White else Color.Black
                        )
                        .pointerInput(currentPage) {
                            if (currentPage == 1) detectVerticalDragGestures { _, dragAmount ->
                                Log.d(dragAmount.toString())
                                if (dragAmount > 0) scope.launch {
                                    pagerState.animateScrollToPage(0, -0.5f)
                                }
                            }
                        }
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AnimatedVisibility(
                        visible = currentPage == 0,
                        modifier =  Modifier
                            .weight(0.9f),
                        label = "playback_control",
                        enter = slideInVertically { h -> -h / 5 },
                        exit = slideOutVertically { h -> -h }
                    ) {
                        PlaybackControl(
                            sliderProgress = player.playbackItem.playbackProgress.value,
                            onProgressChange = { p -> vm.onProgressChange(p, player) },
                            onProgressChangeFinished = { vm.onProgressChangeFinished(player) },
                            repeatState = vm.repeatState,
                            onRepeatStateChange = { vm.onRepeatStateChange(context) },
                            shuffle = vm.shuffle,
                            onShuffleChange = { vm.onShuffleChange(context) },
                            onNavigateBack = { navHostController.popBackStack() },
                        )
                    }
                    val rotate by animateFloatAsState(
                        targetValue = if (currentPage == 0) 90f else -90f,
                        label = "rotate_animation"
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "playlist",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .rotate(rotate)
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch {
                                    if (currentPage == 0) pagerState.animateScrollToPage(1)
                                    else pagerState.animateScrollToPage(
                                        0,
                                        pageOffsetFraction = -0.5f
                                    )
                                }
                            }
                    )
                }
                1 -> Playlist(player, isDarkMode, state = state)
            }
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

// TODO implement width > height layout
@Composable
private fun PlaybackControl(
    sliderProgress: Float,
    onProgressChange: (Float) -> Unit,
    repeatState: RepeatState,
    onRepeatStateChange: () -> Unit,
    shuffle: Boolean,
    onShuffleChange: () -> Unit,
    modifier: Modifier = Modifier,
    onProgressChangeFinished: (() -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val player = LocalPlayer.current
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
                    title = player.playbackItem.data.value.displayName,
                    onLike = {},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp)
                )
                val playing by player.playbackItem.isPlaying
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp)
                ) {
                    Control(
                        progress = sliderProgress,
                        onProgressChange = onProgressChange,
                        repeatState = repeatState,
                        onRepeatStateChange = onRepeatStateChange,
                        shuffle = shuffle,
                        onShuffleChange = onShuffleChange,
                        playing = playing,
                        onPlayPause = { player.playPause(!playing) },
                        nextEnabled = player.hasNext(),
                        previousEnabled = player.hasPrevious(),
                        onNext = { player.next() },
                        onPrevious = { player.previous() },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onProgressChangeFinished = onProgressChangeFinished
                    )
                }
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
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 8.dp)
                .size(36.dp)
                .clip(CircleShape)
                .clickable { },
            tint = Color(0xFFF30000)
        )
    }
}

@Composable
private fun Control(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    repeatState: RepeatState,
    onRepeatStateChange: () -> Unit,
    shuffle: Boolean,
    onShuffleChange: () -> Unit,
    playing: Boolean,
    onPlayPause: (Boolean) -> Unit,
    nextEnabled: Boolean,
    previousEnabled: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
    onProgressChangeFinished: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ControlAction(
                painterResId = when(repeatState) {
                    RepeatState.CURRENT -> R.drawable.repeat_current
                    RepeatState.ON -> R.drawable.repeat_queue
                    RepeatState.OFF -> R.drawable.repeat_off
                },
                contentDescription = when(repeatState) {
                    RepeatState.CURRENT -> "repeat current song"
                    RepeatState.ON -> "repeat queue"
                    RepeatState.OFF -> "repeat off"
                },
                onClick = onRepeatStateChange
            )
            ControlAction(
                painterResId = R.drawable.shuffle,
                contentDescription = "shuffle",
                tint = if (shuffle) LocalContentColor.current else Color.Gray,
                onClick = onShuffleChange
            )
        }
        ProgressSlider(
            progress = progress,
            onProgressChange = onProgressChange,
            modifier = Modifier.fillMaxWidth(),
            onProgressChangeFinished = onProgressChangeFinished
        )
        PlaybackState(
            playing = playing,
            onPlayPause = onPlayPause,
            nextEnabled = nextEnabled,
            previousEnabled = previousEnabled,
            onNext = onNext,
            onPrevious = onPrevious
        )
    }
}

@Composable
private fun ControlAction(
    painterResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = painterResId),
        contentDescription = contentDescription,
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        tint = tint
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressSlider(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onProgressChangeFinished: (() -> Unit)? = null
) {
    val activeColor = if (isSystemInDarkTheme()) SoftSilver else WarmCharcoal
    val inactiveColor = if (isSystemInDarkTheme()) WarmCharcoal else SoftSilver
    Slider(
        value = progress,
        onValueChange = onProgressChange,
        modifier = modifier,
        onValueChangeFinished = onProgressChangeFinished,
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

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun PlaybackState(
    playing: Boolean,
    onPlayPause: (Boolean) -> Unit,
    nextEnabled: Boolean,
    previousEnabled: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigateButton(
            painterResId = R.drawable.previous,
            contentDescription = "previous",
            enabled = previousEnabled,
            onClick = onPrevious
        )
        AnimatedContent(targetState = playing, label = "play_pause") {
            Icon(
                painter = painterResource(
                    id = if (it) R.drawable.pause_filled else R.drawable.play_filled
                ),
                contentDescription = "previous",
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = { onPlayPause(!playing) }
                    )
            )
        }
        NavigateButton(
            painterResId = R.drawable.next,
            contentDescription = "next",
            enabled = nextEnabled,
            onClick = onNext
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun NavigateButton(
    painterResId: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = painterResId),
        contentDescription = contentDescription,
        tint = if (enabled) LocalContentColor.current else Color.Gray,
        modifier = modifier
            .size(32.dp)
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource(),
                onClick = onClick,
                enabled = enabled
            )
    )
}

@Composable
private fun Playlist(
    player: Player,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    Songs(
        files = player.files,
        modifier = modifier
            .fillMaxSize()
            .background(playlistBackgroundColor(isDarkMode)),
        state = state,
        showIndicator = true,
        contentPadding = PaddingValues(0.dp)
    )
}