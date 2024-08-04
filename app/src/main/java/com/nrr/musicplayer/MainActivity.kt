package com.nrr.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.nrr.musicplayer.ui.theme.MusicPlayerTheme
import com.nrr.musicplayer.view.Main

class MainActivity : ComponentActivity() {
    @SuppressLint("ComposableNaming")
    @Composable
    private fun adjustSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
            isAppearanceLightStatusBars = isSystemInDarkTheme()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                adjustSystemBars()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { _ ->
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .consumeWindowInsets(WindowInsets.navigationBars)
                    ) {
                        Main()
                    }
                }
            }
        }
    }
}