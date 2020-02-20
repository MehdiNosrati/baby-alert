package io.mns.baby

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import java.lang.Exception
import java.util.*


private const val notificationId = 1378
private const val channelId = "baby_alert_notification"

class AlertService : Service(), MessageReceiver.MessageListener {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        MessageReceiver.bindListener(this)
        startForeground(notificationId, notification())
        player = (application as? AlertApplication)?.getPlayer()
        vibrator = (application as? AlertApplication)?.getVibrator()
    }

    override fun messageReceived(message: String) {
        if (message.toLowerCase(Locale.US) == "alert")
            alert()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopAlert()
        return START_STICKY
    }

    @Suppress("DEPRECATION")
    private fun alert() {
        val afd = assets.openFd("alert.mp3")
        try {
            player?.reset()
            player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player?.prepare()
            player?.setOnPreparedListener {
                player?.start()
                player?.isLooping = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(
                            300,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator?.vibrate(450)
                }
            }
        } catch (e: Exception) {}
    }

    private fun stopAlert() {
        if (player?.isPlaying == true) {
            player?.stop()
        }
    }

    private fun notification(): Notification {
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setContentTitle("Baby alert is active...")
        builder.setContentText("Taking care of your baby \uD83E\uDD17")
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 126, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        builder.setOngoing(true)
        val notification = builder.build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Baby alert notification channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        return notification
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlert()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}