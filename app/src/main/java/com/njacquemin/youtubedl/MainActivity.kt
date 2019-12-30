package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val yt = YoutubeDownloader(this)
        yt.download("https://www.youtube.com/watch?v=BaW_jenozKc")
    }
}
