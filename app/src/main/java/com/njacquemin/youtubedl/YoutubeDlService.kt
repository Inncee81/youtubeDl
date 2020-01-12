package com.njacquemin.youtubedl

import android.app.IntentService
import android.content.Intent
import java.io.File
import android.app.PendingIntent

const val PENDING_RESULT_EXTRA = "PENDING_RESULT"
const val LINK= "LINK"
const val FILEPATH = "FILEPATH"
const val RESULT = "RESULT"

const val RESULT_CODE = 0

class YoutubeDlService: IntentService("YoutubeDownloadService") {
    private lateinit var youtube: YoutubeDownloader

    override fun onCreate() {
        super.onCreate()
        youtube = YoutubeDownloader(this, null)
    }

    override fun onHandleIntent(intent: Intent?) {
        val link = intent!!.getStringExtra(LINK)
        val outputFile = File(intent.getStringExtra(FILEPATH))

        val reply: PendingIntent = intent.getParcelableExtra(PENDING_RESULT_EXTRA)

        youtube.download(link, outputFile) {_ ->
            val intent = Intent()
            intent.putExtra(LINK, link)
            reply.send(this,  RESULT_CODE, intent )
        }
    }

}