package com.nrr.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.util.concurrent.MoreExecutors
import com.nrr.musicplayer.media.Player
import com.nrr.musicplayer.model.AudioFile
import com.nrr.musicplayer.model.AudioFiles
import com.nrr.musicplayer.service.PlaybackService
import com.nrr.musicplayer.ui.theme.MusicPlayerTheme
import com.nrr.musicplayer.util.Destination
import com.nrr.musicplayer.util.Log
import com.nrr.musicplayer.util.minApiLevel
import com.nrr.musicplayer.view.Main
import com.nrr.musicplayer.view.Playback

val LocalAudioFilesLoader = compositionLocalOf<() -> AudioFiles> { { AudioFiles() } }
val LocalPermissionGranted = compositionLocalOf { false }
val LocalPlayer = compositionLocalOf { Player(null) }
val Context.dataStore by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {
    private var mediaController: MediaController? by mutableStateOf(null)
    private var player: Player by mutableStateOf(Player(null))

    @SuppressLint("ComposableNaming")
    @Composable
    private fun adjustSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
            isAppearanceLightStatusBars = true
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

    private fun musicAudioAccessPermitted(): Boolean = minApiLevel(
        minApiLevel = Build.VERSION_CODES.TIRAMISU,
        onApiLevelRange = {
            checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        },
        onApiLevelBelow = {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    )

    private fun requestPermission(launcher: ManagedActivityResultLauncher<String, Boolean>) {
        launcher.launch(
            input = minApiLevel(
                minApiLevel = Build.VERSION_CODES.TIRAMISU,
                onApiLevelRange = { Manifest.permission.READ_MEDIA_AUDIO },
                onApiLevelBelow = { Manifest.permission.READ_EXTERNAL_STORAGE }
            )
        )
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController?.release()
        player.restart()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun App() {
        LaunchedEffect(true) {
            val token = SessionToken(
                this@MainActivity,
                ComponentName(this@MainActivity, PlaybackService::class.java)
            )
            MediaController.Builder(this@MainActivity, token).buildAsync().apply {
                addListener(
                    {
                        mediaController?.removeListener(player)
                        mediaController = get().apply {
                            player = Player.create(this)
                            player.listen()
                            addListener(this@MainActivity.player)
                        }
                    },
                    MoreExecutors.directExecutor()
                )
            }
        }
        var permissionGranted by rememberSaveable {
            mutableStateOf(false)
        }
        var showWarning by rememberSaveable {
            mutableStateOf(false)
        }
        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) permissionGranted = true
            else if (!musicAudioAccessPermitted()) showWarning = true
            else permissionGranted = true
        }
        LaunchedEffect(true) {
            if (!musicAudioAccessPermitted()) requestPermission(permissionLauncher)
            else permissionGranted = true
        }
        CompositionLocalProvider(value = LocalAudioFilesLoader provides { loadAudioFiles() }) {
            CompositionLocalProvider(value = LocalPermissionGranted provides permissionGranted) {
                CompositionLocalProvider(value = LocalPlayer provides player) {
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
                                val controller = rememberNavController()
                                NavHost(
                                    navController = controller,
                                    startDestination = Destination.Main()
                                ) {
                                    composable(Destination.Main()) { Main(controller) }
                                    composable(
                                        route = Destination.Playback(),
                                        enterTransition = { slideInVertically { it } },
                                        exitTransition = { slideOutVertically { it } }
                                    ) {
                                        Playback(controller)
                                    }
                                }
                            }
                            PermissionWarning(
                                show = showWarning,
                                onRequestPermission = {
                                    showWarning = false
                                    requestPermission(permissionLauncher)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionWarning(
        show: Boolean,
        onRequestPermission: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        if (show) Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = "This app requires access to your music and audio files"
                )
                TextButton(
                    onClick = onRequestPermission,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "OK",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}