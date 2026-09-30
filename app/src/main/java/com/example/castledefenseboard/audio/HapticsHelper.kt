package com.example.castledefenseboard.audio

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticsHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun vibratePlacement(enabled: Boolean) {
        if (!enabled) return
        vibrateSimple(durationMs = 25, amplitude = 50)
    }

    fun vibrateEnemyDefeated(enabled: Boolean) {
        if (!enabled) return
        vibrateSimple(durationMs = 18, amplitude = 40)
    }

    fun vibrateCastleDamage(enabled: Boolean) {
        if (!enabled) return
        vibrateSimple(durationMs = 120, amplitude = 180)
    }

    fun vibrateVictory(enabled: Boolean) {
        if (!enabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 70, 50, 70, 120)
            val amplitudes = intArrayOf(0, 80, 0, 120, 0, 220)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 50, 70, 50, 70, 120), -1)
        }
    }

    fun vibrateDefeat(enabled: Boolean) {
        if (!enabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 150, 80, 200)
            val amplitudes = intArrayOf(0, 160, 0, 220)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 150, 80, 200), -1)
        }
    }

    private fun vibrateSimple(durationMs: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    durationMs,
                    amplitude.coerceIn(1, 255)
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(durationMs)
        }
    }
}
