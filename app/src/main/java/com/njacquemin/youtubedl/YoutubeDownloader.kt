package com.njacquemin.youtubedl

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File

private const val FFMPEG_TAG = "FFMPEG"

class YoutubeDownloader(val context: Context, val callbacks: Callbacks?) {

    interface Callbacks {
        fun onFfmpegStart()
        fun onFfmpegProgress(message: String?)
        fun onFfmpegFailure(message: String?)
        fun onFfmpegSuccess(message: String?)
        fun onFfmpegFinish()
    }

    private val handlerThread = HandlerThread("backgroundThread").apply {
        start()
    }
    private val backgroundHandler = Handler(handlerThread.looper)

    private val mainHandler = Handler(Looper.getMainLooper())

    private val ffmpeg = FFmpeg.getInstance(context)!!

    private var ytDownloader: PyObject

    fun download(link: String, output: File, doneCallback: (success: Boolean) -> Unit) {
        backgroundHandler.post {
            ytDownloader.callAttr("download", arrayOf(link))

            while (ffmpeg.isFFmpegCommandRunning) {
                Thread.sleep(1000)
            }
            ffmpeg.execute(
                arrayOf("-i", File(context.filesDir, "yt-dl.tmp").absolutePath, output.absolutePath),
                object : ExecuteBinaryResponseHandler() {

                    override fun onStart() {
                        Log.d(FFMPEG_TAG, "Start")
                        callbacks?.onFfmpegStart()
                    }

                    override fun onProgress(message: String?) {
                        Log.d(FFMPEG_TAG, "Progress: $message")
                        callbacks?.onFfmpegProgress(message)
                    }

                    override fun onFailure(message: String?) {
                        Log.d(FFMPEG_TAG, "Failure: $message")
                        callbacks?.onFfmpegFailure(message)
                        mainHandler.post {
                            doneCallback(false)
                        }
                    }

                    override fun onSuccess(message: String?) {
                        Log.d(FFMPEG_TAG, "Success: $message")
                        callbacks?.onFfmpegSuccess(message)
                        mainHandler.post {
                            doneCallback(true)
                        }
                    }

                    override fun onFinish() {
                        Log.d(FFMPEG_TAG, "Finish")
                        callbacks?.onFfmpegFinish()
                    }
                })
        }
    }

    init {
        ffmpeg.loadBinary(null) // It will fail with exception if it cannot be loaded. This should not happen

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }

        val py = Python.getInstance()

        val options = py.builtins.callAttr("dict")
        options.callAttr("__setitem__", "outtmpl", File(context.filesDir, "yt-dl.tmp").absolutePath)
        options.callAttr("__setitem__", "format", "bestaudio/best")

        val yt = py.getModule("youtube_dl")
        ytDownloader = yt.callAttr("YoutubeDL", options)

    }
}