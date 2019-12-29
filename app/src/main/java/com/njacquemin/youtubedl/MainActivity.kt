package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.Python

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Python.start(AndroidPlatform(this))

        val py = Python.getInstance()
        val os = py.getModule("os")
        val path = os["path"]?.callAttr("join", "test", "path")
        Log.e("MAIN", path.toString())
    }
}
