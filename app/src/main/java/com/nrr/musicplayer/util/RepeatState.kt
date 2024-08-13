package com.nrr.musicplayer.util

enum class RepeatState {
    ON, CURRENT, OFF;

    companion object {
        fun next(current: RepeatState): RepeatState = when (current) {
            ON -> CURRENT
            CURRENT -> OFF
            OFF -> ON
        }

        fun from(value: Int): RepeatState = when (value) {
            0 -> ON
            1 -> CURRENT
            else -> OFF
        }
    }
}