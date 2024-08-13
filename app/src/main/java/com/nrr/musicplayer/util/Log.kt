package com.nrr.musicplayer.util
import android.util.Log as l

object Log {
    private const val TAG = "MPLog"

    fun d(message: String) = l.d(TAG, message)
    fun i(message: String) = l.i(TAG, message)
    fun w(message: String) = l.w(TAG, message)
}