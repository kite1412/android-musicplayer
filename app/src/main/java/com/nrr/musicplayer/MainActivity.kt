package com.nrr.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.nrr.musicplayer.model.AudioFile
import com.nrr.musicplayer.model.AudioFiles
import com.nrr.musicplayer.ui.theme.MusicPlayerTheme
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.view.Main

val LocalAudioFilesLoader = compositionLocalOf<() -> AudioFiles> { { AudioFiles() } }

class MainActivity : ComponentActivity() {
    @SuppressLint("ComposableNaming")
    @Composable
    private fun adjustSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
            isAppearanceLightStatusBars = isSystemInDarkTheme()
        }
    }

    private fun loadAudioFiles(): AudioFiles {
        val projection = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
        )
        val sort = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sort
        )
        val audioFiles = mutableListOf<AudioFile>()
        var error: Throwable? = null
        try {
            cursor?.let {
                while (it.moveToNext()) {
                    audioFiles.add(
                        AudioFile(
                            it.getString(0),
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
                        )
                    )
                }
                return@let
            }
            Log.d("audio files size: ${audioFiles.size}")
        } catch (e: Throwable) {
            error = e
        } finally {
            cursor?.close()
        }
        return AudioFiles(audioFiles, error)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(value = LocalAudioFilesLoader provides { loadAudioFiles() }) {
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
}