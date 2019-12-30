package com.njacquemin.youtubedl

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File

class YoutubeDownloader(val context: Context, val callbacks: Callbacks?) {

    interface Callbacks {
        fun onFfmpegStart()
        fun onFfmpegProgress(message: String?)
        fun onFfmpegFailure(message: String?)
        fun onFfmpegSuccess(message: String?)
        fun onFfmpegFinish()
    }

    private val ffmpeg = FFmpeg.getInstance(context)!!

    private var ytDownloader: PyObject

    @Synchronized
    fun download(link: String, output: File){
        ytDownloader.callAttr("download", arrayOf(link))

        ffmpeg.execute(arrayOf("-i", File(context.filesDir, "yt-dl.tmp").absolutePath, output.absolutePath), object : ExecuteBinaryResponseHandler() {

            override fun onStart() {callbacks?.onFfmpegStart()}

            override fun onProgress(message: String?) {callbacks?.onFfmpegProgress(message)}

            override fun onFailure(message: String?) {callbacks?.onFfmpegFailure(message)}

            override fun onSuccess(message: String?) {callbacks?.onFfmpegSuccess(message)}

            override fun onFinish() {callbacks?.onFfmpegFinish()}
        })
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