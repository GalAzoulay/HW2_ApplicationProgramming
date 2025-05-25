package com.example.hw2.utilities

import android.content.Context
import android.media.MediaPlayer
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SingleSoundPlayer(context: Context) {
    private val context: Context = context.applicationContext
    private val executor: Executor = Executors.newSingleThreadExecutor()
    fun playSound(resID: Int) {
        executor.execute {
            val mediaPlayer = MediaPlayer.create(context, resID)
            mediaPlayer.isLooping = false
            mediaPlayer.setVolume(1.0f, 1.0f)
            mediaPlayer.start()
            mediaPlayer.setOnSeekCompleteListener { mp: MediaPlayer? ->
                var mpl = mp
                mpl!!.stop()
                mpl.release()
                mpl = null
            }
        }
    }
}