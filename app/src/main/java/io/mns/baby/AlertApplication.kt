package io.mns.baby

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator

class AlertApplication : Application() {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun getPlayer(): MediaPlayer {
        if (player == null) {
            player = MediaPlayer()
        }
        return player!!
    }

    fun getVibrator(): Vibrator {
        if (vibrator == null) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        return vibrator!!
    }


}