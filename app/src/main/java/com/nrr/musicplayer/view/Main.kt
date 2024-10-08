package com.nrr.musicplayer.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nrr.musicplayer.LocalPlayer
import com.nrr.musicplayer.R
import com.nrr.musicplayer.model.noData
import com.nrr.musicplayer.ui.theme.WarmCharcoal
import com.nrr.musicplayer.util.Destination
import com.nrr.musicplayer.util.ScrollConnection
import com.nrr.musicplayer.util.sharedViewModel
import com.nrr.musicplayer.view_model.MainViewModel

private val playBarPadding = 16.dp
private val playBarHeight = 70.dp

@Composable
fun Main(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    vm: MainViewModel = viewModel(MainViewModel::class)
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val config = LocalConfiguration.current
    var minHeaderHeight by remember {
        mutableStateOf(0.dp)
    }
    val totalHeaderHeight by remember {
        derivedStateOf { config.screenHeightDp.dp / 4 + statusBarHeight }
    }
    var headerHeight by remember {
        mutableStateOf(totalHeaderHeight)
    }
    val sharedViewModel = sharedViewModel()
    var titleAlpha by remember {
        mutableFloatStateOf(1f)
    }
    val density = LocalDensity.current
    val state = rememberLazyListState()
    val expandHeader by remember {
        derivedStateOf {
            state.firstVisibleItemScrollOffset == 0 && state.firstVisibleItemIndex == 0
        }
    }
    var collapsedOrExpanded by remember(expandHeader) {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(
                with(density) {
                    ScrollConnection(
                        consume = !collapsedOrExpanded
                    ) {
                        val value = it.toDp()
                        if (value <= 0.dp) if (value >= headerHeight) headerHeight = minHeaderHeight
                        else if (headerHeight + value > minHeaderHeight) headerHeight += value
                        else headerHeight = minHeaderHeight
                        else if (value >= totalHeaderHeight) headerHeight = totalHeaderHeight
                        else if (headerHeight + value < totalHeaderHeight) headerHeight += value
                        else headerHeight = totalHeaderHeight
                        collapsedOrExpanded =
                            headerHeight == totalHeaderHeight || headerHeight == minHeaderHeight
                        titleAlpha =
                            if (value <= 0.dp) (headerHeight - minHeaderHeight) / totalHeaderHeight
                            else headerHeight / totalHeaderHeight
                    }
                }
            )
    ) {
        AnimatedVisibility(
            visible = vm.animate,
            enter = slideInVertically { -it }
        ) {
            Songs(
                files = sharedViewModel.audioFiles,
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentPadding = PaddingValues(
                    top = headerHeight + 16.dp,
                    bottom = playBarHeight + playBarPadding * 2
                )
            )
        }
        AnimatedVisibility(
            visible = vm.animate,
            enter = slideInVertically()
        ) {
            Header(
                vm = vm,
                modifier = Modifier.height(headerHeight),
                titleAlpha = titleAlpha
            ) { minHeaderHeight = it + statusBarHeight }
        }
        val playback by LocalPlayer.current.playbackItem.data
        AnimatedVisibility(
            visible = vm.animate && !playback.noData(),
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it / 2 },
            exit = slideOutVertically { it },
        ) {
            PlayBar {
                navHostController.navigate(Destination.Playback())
            }
        }
    }
}

@Composable
private fun Header(
    vm: MainViewModel,
    modifier: Modifier = Modifier,
    titleAlpha: Float = 1f,
    menusHeight: (Dp) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(configuration.screenHeightDp.dp / 4)
            .clip(
                RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp,
                )
            )
            .background(WarmCharcoal)
    ) {
        val density = LocalDensity.current
        var titleBottomPadding by remember {
            mutableStateOf(0.dp)
        }
        Text(
            text = "Music Player",
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = titleBottomPadding)
                .alpha(titleAlpha),
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp
        )
        Menus(
            vm = vm,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onGloballyPositioned {
                    with(density) {
                        val height = it.size.height.toDp()
                        titleBottomPadding = height
                        menusHeight(height)
                    }
                }
        )
    }
}

@Composable
private fun Menus(
    modifier: Modifier = Modifier,
    vm: MainViewModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        vm.menus.forEach {
            Menu(
                menu = it,
                selected = it == vm.currentMenu,
                onClick = { m -> vm.currentMenu = m }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    menu: String,
    selected: Boolean,
    onClick: (String) -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Gray,
        label = "textColor"
    )
    val defaultFontSize = MaterialTheme.typography.bodyLarge.fontSize.value
    val fontSize by animateFloatAsState(
        targetValue = if (selected) defaultFontSize else defaultFontSize - 2f,
        label = "fontSize"
    )
    Text(
        text = menu,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { onClick(menu) },
        color = color,
        fontSize = fontSize.sp
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun PlayBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(playBarPadding)
            .height(playBarHeight)
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource(),
                onClick = onClick
            )
            .clip(RoundedCornerShape(16.dp))
            .background(WarmCharcoal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val player = LocalPlayer.current
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(0.8f)
                ) {
                    MusicNoteIcon(
                        backgroundColor = Color.White,
                        tint = Color.Black
                    )
                    SlidingText(
                        text = player.playbackItem.data.value.displayName,
                        style = LocalTextStyle.current.copy(
                            color = Color.White
                        )
                    )
                }
                PlayBarActions(
                    playing = player.playbackItem.isPlaying.value,
                    onStateChange = { player.playPause(it) },
                    onClose = { player.clear() },
                    modifier = Modifier.weight(0.2f)
                )
            }
            LinearProgressIndicator(
                progress = { player.playbackItem.playbackProgress.value },
                modifier = Modifier
                    .width(this@BoxWithConstraints.maxWidth * 0.8f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .weight(0.1f),
                trackColor = Color.Black,
                color = Color.White
            )
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun PlayBarActions(
    playing: Boolean,
    onStateChange: (playing: Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = Color.White
        AnimatedContent(
            targetState = playing,
            label = "state"
        ) {
            Icon(
                painter = painterResource(id = if (!it) R.drawable.play else R.drawable.pause),
                contentDescription = if (!it) "start" else "pause",
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ) { onStateChange(!it) },
                tint = tint
            )
        }
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "close",
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource(),
                    onClick = onClose
                ),
            tint = tint
        )
    }
}