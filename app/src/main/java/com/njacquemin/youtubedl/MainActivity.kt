package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val yt = YoutubeDownloader(this, null)
        yt.download("https://www.youtube.com/watch?v=BaW_jenozKc", File("sdcard", "test.mp3"))

        // Check how it was started and if we can get the youtube link
        Log.e("YTDOWNLOADER", "check...")
        if (savedInstanceState == null && Intent.ACTION_SEND == intent.action
            && intent.type != null && "text/plain" == intent.type
        ) {

            val ytLink = intent.getStringExtra(Intent.EXTRA_TEXT)
            Log.e("YTDOWNLOADER", "Got shared link: $ytLink")

            /*if (ytLink != null && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v="))) {
                youtubeLink = ytLink
                // We have a valid link
                getYoutubeDownloadUrl(youtubeLink)
            } else {
                Toast.makeText(this, "No yt link", Toast.LENGTH_LONG).show()
                finish()
            }*/
        } /*else if (savedInstanceState != null && youtubeLink != null) {
            getYoutubeDownloadUrl(youtubeLink)
        } else {
            finish()
        }*/
    }
}
