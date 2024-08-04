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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nrr.musicplayer.R
import com.nrr.musicplayer.view_model.MainViewModel

@Composable
fun Main(
    modifier: Modifier = Modifier,
    vm: MainViewModel = viewModel(modelClass = MainViewModel::class)
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp / 4 + statusBarHeight
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = vm.animate,
            enter = slideInVertically()
        ) {
            Header(
                vm = vm,
                modifier = Modifier.height(headerHeight)
            )
        }
        AnimatedVisibility(
            visible = vm.animate && vm.showPlayBar,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it / 2 },
            exit = slideOutVertically { it },
        ) {
            PlayBar(vm)
        }
    }
}

@Composable
private fun Header(
    vm: MainViewModel,
    modifier: Modifier = Modifier
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
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        val density = LocalDensity.current
        var titleBottomPadding by remember {
            mutableStateOf(0.dp)
        }
        Text(
            text = "Music Player",
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = titleBottomPadding)
        )
        Menus(
            vm = vm,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onGloballyPositioned {
                    with(density) {
                        titleBottomPadding = it.size.height.toDp()
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
        targetValue = if (selected) if (!isSystemInDarkTheme()) Color.White
            else Color.Black
                else Color.Gray,
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

@Composable
private fun PlayBar(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(16.dp)
            .height(70.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    MusicNoteIcon()
                    SlidingText(text = "asdddddddddddddddddd")
                }
                PlayBarActions(
                    playing = vm.playing,
                    onStateChange = { vm.playing = it },
                    onClose = { vm.showPlayBar = false },
                    modifier = Modifier.weight(0.2f)
                )
            }
            LinearProgressIndicator(
                progress = { 0.5f },
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
        val tint = if (isSystemInDarkTheme()) Color.Black else Color.White
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