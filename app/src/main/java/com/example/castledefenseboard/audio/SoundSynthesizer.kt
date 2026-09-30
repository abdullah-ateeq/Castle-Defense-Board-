package com.example.castledefenseboard.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundSynthesizer {

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playUnitPlaced() {
        scope.launch {
            // Crisp two-tone chime (523Hz then 659Hz)
            val durationMs = 120
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val freq = if (i < samples / 2) 523.25 else 659.25
                val envelope = 1.0 - (i.toDouble() / samples)
                val sample = (sin(2.0 * PI * freq * t) * envelope * 0.45 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playUnitMoved() {
        scope.launch {
            // Stone sliding clink
            val durationMs = 90
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val freq = 350.0 + (i.toDouble() / samples) * 200.0
                val envelope = 1.0 - (i.toDouble() / samples)
                val sample = (sin(2.0 * PI * freq * t) * envelope * 0.35 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playGuardAttack() {
        scope.launch {
            // Heavy shield bash / strike (140Hz with rapid decay and slight noise)
            val durationMs = 140
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val envelope = kotlin.math.exp(-t * 28.0)
                val fundamental = sin(2.0 * PI * 130.0 * t)
                val harmonic = sin(2.0 * PI * 260.0 * t) * 0.4
                val sample = ((fundamental + harmonic) * envelope * 0.6 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playArcherAttack() {
        scope.launch {
            // Arrow release twang / whoosh (880Hz down to 440Hz fast)
            val durationMs = 110
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val progress = i.toDouble() / samples
                val freq = 880.0 - progress * 400.0
                val t = i.toDouble() / sampleRate
                val envelope = 1.0 - progress
                val sample = (sin(2.0 * PI * freq * t) * envelope * 0.4 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playMageAttack() {
        scope.launch {
            // Arcane shimmer (two high harmonic frequencies combined)
            val durationMs = 180
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / samples
                val envelope = kotlin.math.sin(progress * PI) * (1.0 - progress * 0.5)
                val s1 = sin(2.0 * PI * 660.0 * t)
                val s2 = sin(2.0 * PI * 990.0 * t) * 0.6
                val s3 = sin(2.0 * PI * 1320.0 * t) * 0.3
                val sample = ((s1 + s2 + s3) * envelope * 0.35 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playEnemyDefeated() {
        scope.launch {
            // Subdued crumble / impact
            val durationMs = 100
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val progress = i.toDouble() / samples
                val freq = 200.0 - progress * 80.0
                val t = i.toDouble() / sampleRate
                val envelope = 1.0 - progress
                val sample = (sin(2.0 * PI * freq * t) * envelope * 0.4 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCastleHit() {
        scope.launch {
            // Heavy warning gong / impact (85Hz deep pulse)
            val durationMs = 280
            val samples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val t = i.toDouble() / sampleRate
                val envelope = kotlin.math.exp(-t * 10.0)
                val fundamental = sin(2.0 * PI * 85.0 * t)
                val grit = sin(2.0 * PI * 170.0 * t) * 0.5
                val sample = ((fundamental + grit) * envelope * 0.7 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playVictory() {
        scope.launch {
            // Heroic triad fanfare: C5 (523Hz), E5 (659Hz), G5 (784Hz), High C6 (1046Hz)
            val notes = listOf(523.25, 659.25, 784.0, 1046.5)
            val noteDurationMs = 120
            val samplesPerNote = sampleRate * noteDurationMs / 1000
            val totalSamples = samplesPerNote * notes.size
            val buffer = ShortArray(totalSamples)

            notes.forEachIndexed { noteIdx, freq ->
                val offset = noteIdx * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / samplesPerNote
                    val env = 1.0 - (progress * 0.7)
                    val sample = (sin(2.0 * PI * freq * t) * env * 0.45 * Short.MAX_VALUE).toInt()
                    buffer[offset + i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playDefeat() {
        scope.launch {
            // Somber descending cadence: E4 (329.6), D4 (293.7), C4 (261.6)
            val notes = listOf(329.63, 293.66, 220.0)
            val noteDurationMs = 180
            val samplesPerNote = sampleRate * noteDurationMs / 1000
            val totalSamples = samplesPerNote * notes.size
            val buffer = ShortArray(totalSamples)

            notes.forEachIndexed { noteIdx, freq ->
                val offset = noteIdx * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / samplesPerNote
                    val env = kotlin.math.exp(-progress * 2.5)
                    val sample = (sin(2.0 * PI * freq * t) * env * 0.45 * Short.MAX_VALUE).toInt()
                    buffer[offset + i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                val delayTime = (buffer.size * 1000L / sampleRate) + 80
                kotlinx.coroutines.delay(delayTime)
                runCatching {
                    audioTrack.stop()
                    audioTrack.release()
                }
            }
        } catch (_: Exception) {
            // Silently handle if audio track cannot be acquired
        }
    }
}
