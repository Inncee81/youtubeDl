package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.Python
import com.chaquo.python.PyObject



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Python.start(AndroidPlatform(this))

        val py = Python.getInstance()

        val d = py.builtins.callAttr("dict")
        d.callAttr("__setitem__", "outtmpl", "sdcard/%(title)s-%(id)s.%(ext)s")

        val yt = py.getModule("youtube_dl")
        val ydl = yt.callAttr("YoutubeDL", d)
        ydl.callAttr("download", arrayOf("https://www.youtube.com/watch?v=BaW_jenozKc"))
        Log.e("MAIN", "----------------- DONE ----------------")
    }
}
