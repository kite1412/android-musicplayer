package com.nrr.musicplayer.util

enum class Destination {
    Main,
    Playback;

    operator fun invoke(): String = when(this) {
        Main -> "main"
        Playback -> "playback"
    }
}