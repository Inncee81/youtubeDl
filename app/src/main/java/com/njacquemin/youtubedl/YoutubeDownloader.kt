package com.njacquemin.youtubedl

import android.content.Context
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException

class YoutubeDownloader(val context: Context) {

    private val ffmpeg = FFmpeg.getInstance(context)!!

    private var ytDownloader: PyObject

    @Synchronized
    fun download(link: String){
        ytDownloader.callAttr("download", arrayOf(link))

        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(arrayOf("-i", "/sdcard/yt-dl.m4a", "/sdcard/yt-dl.mp3"), object : ExecuteBinaryResponseHandler() {

                override fun onStart() {Log.e("MAIN", "onStart")}

                override fun onProgress(message: String?) {Log.e("MAIN", "onProgress $message")}

                override fun onFailure(message: String?) {Log.e("MAIN", "onFailure $message")}

                override fun onSuccess(message: String?) {Log.e("MAIN", "onSuccess")}

                override fun onFinish() {Log.e("MAIN", "onFinish")}
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // Handle if FFmpeg is already running
            Log.e("MAIN", "ffmpeg already running")
        }
    }

    init {
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {

                override fun onStart() {
                    Log.e("MAIN", "onStart")
                }

                override fun onFailure() {
                    Log.e("MAIN", "onFailure")
                }

                override fun onSuccess() {
                    Log.e("MAIN", "onSuccess")
                }

                override fun onFinish() {
                    Log.e("MAIN", "onFinish")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            Log.e("MAIN", "ffmpegNotSupported")
            // Handle if FFmpeg is not supported by device
        }

        Python.start(AndroidPlatform(context))

        val py = Python.getInstance()

        val options = py.builtins.callAttr("dict")
        options.callAttr("__setitem__", "outtmpl", "/sdcard/yt-dl.%(ext)s")
        options.callAttr("__setitem__", "format", "bestaudio/best")

        val yt = py.getModule("youtube_dl")
        ytDownloader = yt.callAttr("YoutubeDL", options)

    }
}