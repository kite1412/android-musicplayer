package com.nrr.musicplayer.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nrr.musicplayer.LocalPlayer
import com.nrr.musicplayer.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playback(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    VerticalPager(
        state = rememberPagerState { 2 },
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars),
        userScrollEnabled = false
    ) {
        when (it) {
            0 -> PlaybackControl {
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
    modifier: Modifier = Modifier,
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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        SlidingText(
            text = title,
            style = LocalTextStyle.current.copy(
                fontSize = 20.sp,
            ),
            modifier = Modifier.weight(0.9f)
        )
        IconButton(
            onClick = { onLike(false) },
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.heart_outlined),
                contentDescription = "like / undo",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFF30000)
            )
        }
    }
}