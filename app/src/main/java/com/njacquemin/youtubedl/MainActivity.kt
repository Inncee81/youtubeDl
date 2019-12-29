package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.Python
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ffmpeg = FFmpeg.getInstance(this)
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {

                override fun onStart() {Log.e("MAIN", "onStart")}

                override fun onFailure() {Log.e("MAIN", "onFailure")}

                override fun onSuccess() {Log.e("MAIN", "onSuccess")}

                override fun onFinish() {Log.e("MAIN", "onFinish")}
            })
        } catch (e: FFmpegNotSupportedException) {
            Log.e("MAIN", "ffmpegNotSupported")
            // Handle if FFmpeg is not supported by device
        }


        Python.start(AndroidPlatform(this))

        val py = Python.getInstance()

        val options = py.builtins.callAttr("dict")
        options.callAttr("__setitem__", "outtmpl", "/sdcard/yt-dl.%(ext)s")
        options.callAttr("__setitem__", "format", "bestaudio/best")

        val yt = py.getModule("youtube_dl")
        val ydl = yt.callAttr("YoutubeDL", options)
        ydl.callAttr("download", arrayOf("https://www.youtube.com/watch?v=BaW_jenozKc"))
        Log.e("MAIN", "----------------- DONE ----------------")

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
}
