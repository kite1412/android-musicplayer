package com.nrr.musicplayer.view

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nrr.musicplayer.view_model.MainViewModel

@Composable
fun Main(
    modifier: Modifier = Modifier,
    vm: MainViewModel = viewModel(modelClass = MainViewModel::class)
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp / 4 + statusBarHeight
    Box(modifier = modifier.fillMaxSize()) {
        Header(
            vm = vm,
            modifier = Modifier.height(headerHeight)
        )

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
            color = Color.White,
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
        targetValue = if (selected) MaterialTheme.colorScheme.primary
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