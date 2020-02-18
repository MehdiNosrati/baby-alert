package io.mns.baby

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieDrawable
import io.mns.baby.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: MediaPlayer
    private lateinit var vibrator: Vibrator
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        setInitialAnimation()
        setListeners()
    }

    private fun setInitialAnimation() {
        binding.animation.setAnimation("sleepy.json")
        binding.animation.playAnimation()
        binding.animation.repeatCount = LottieDrawable.INFINITE
    }

    private fun setListeners() {

        binding.alert.setOnClickListener {
            alert()
            binding.calmDown.animate().translationY(0f).alpha(1f).setDuration(300).start()
            binding.alert.animate().alpha(0f).setDuration(300).start()
        }

        binding.calmDown.setOnClickListener {
            stopAlert()
            binding.calmDown.animate()
                .translationY(resources.getDimensionPixelSize(R.dimen._100).toFloat())
                .alpha(0f)
                .setDuration(300).start()
            binding.alert.animate().alpha(1f).setDuration(300).start()
        }

    }

    private fun alert() {
        binding.animation.setAnimation("alert.json")
        binding.animation.playAnimation()
        binding.animation.repeatCount = LottieDrawable.INFINITE

        val afd = assets.openFd("alert.mp3")
        player = MediaPlayer()
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player.prepare()
        player.start()
        player.isLooping = true
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.EFFECT_HEAVY_CLICK
                    )
                )
            } else {
                vibrator.vibrate(450)
            }
        }, 0, 1000)
    }

    private fun stopAlert() {
        setInitialAnimation()
        player.stop()
        timer?.cancel()
        timer = null
    }
}
