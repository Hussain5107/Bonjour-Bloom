package com.example.bonjourbloom.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

enum class RecordingState {
    IDLE,
    RECORDING,
    RECORDED,
    PLAYING,
    ERROR
}

class AudioRecorderService(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentOutputFile: File? = null

    var state: RecordingState = RecordingState.IDLE
        private set

    fun getCurrentFile(): File? = currentOutputFile

    fun getMaxAmplitude(): Int {
        return try {
            if (state == RecordingState.RECORDING) {
                mediaRecorder?.maxAmplitude ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun startRecording(): Boolean {
        stopPlayback()
        return try {
            val audioFile = File(context.cacheDir, "speech_practice_${System.currentTimeMillis()}.mp4")
            currentOutputFile = audioFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(96000)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            state = RecordingState.RECORDING
            true
        } catch (e: Exception) {
            Log.e("AudioRecorderService", "Failed to start recording", e)
            state = RecordingState.ERROR
            false
        }
    }

    fun stopRecording(): Boolean {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            state = RecordingState.RECORDED
            true
        } catch (e: Exception) {
            Log.e("AudioRecorderService", "Failed to stop recording", e)
            mediaRecorder = null
            state = RecordingState.ERROR
            false
        }
    }

    fun playRecording(onComplete: () -> Unit) {
        val file = currentOutputFile ?: return
        if (!file.exists()) return

        stopPlayback()
        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    state = RecordingState.RECORDED
                    onComplete()
                }
                start()
            }
            mediaPlayer = player
            state = RecordingState.PLAYING
        } catch (e: Exception) {
            Log.e("AudioRecorderService", "Failed to play recording", e)
            state = RecordingState.ERROR
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        if (state == RecordingState.PLAYING) {
            state = RecordingState.RECORDED
        }
    }

    fun reset() {
        stopPlayback()
        mediaRecorder?.apply {
            try {
                stop()
            } catch (_: Exception) {}
            release()
        }
        mediaRecorder = null
        currentOutputFile?.delete()
        currentOutputFile = null
        state = RecordingState.IDLE
    }
}
