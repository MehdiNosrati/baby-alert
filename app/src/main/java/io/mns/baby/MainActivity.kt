package io.mns.baby

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieDrawable
import io.mns.baby.databinding.ActivityMainBinding
import java.lang.Exception


class MainActivity : AppCompatActivity(), MessageReceiver.MessageListener {
    private lateinit var binding: ActivityMainBinding
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = (application as? AlertApplication)?.getPlayer()
        vibrator = (application as? AlertApplication)?.getVibrator()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setInitialAnimation()
        startService(Intent(this, AlertService::class.java))
        getPermission()
        setListener()
    }

    private fun setListener() {
        MessageReceiver.bindListenerActivity(this)

        binding.exit.setOnClickListener {
            stopService(Intent(this, AlertService::class.java))
            finish()
        }

        binding.calm.setOnClickListener {
            if (it.visibility == View.VISIBLE) {
                startService(Intent(this, AlertService::class.java))
                stopAlert()
            }
        }
    }

    private fun getPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
            1
        )
    }

    private fun setInitialAnimation() {
        binding.animation.setAnimation("sleepy.json")
        binding.animation.playAnimation()
        binding.animation.repeatCount = LottieDrawable.INFINITE
    }

    @Suppress("DEPRECATION")
    private fun alert() {
        binding.calm.visibility = View.VISIBLE
        binding.animation.setAnimation("alert.json")
        binding.animation.playAnimation()
        binding.animation.repeatCount = LottieDrawable.INFINITE

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
        binding.calm.visibility = View.GONE
        setInitialAnimation()
        if (player?.isPlaying == true) {
            player?.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlert()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied, it won't work :(",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun messageReceived(message: String) {
        alert()
    }
}
