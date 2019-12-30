package com.njacquemin.youtubedl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import android.content.Intent
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

private fun layoutParam(weight: Float): TableRow.LayoutParams {
    return TableRow.LayoutParams(
        0,
        TableRow.LayoutParams.MATCH_PARENT,
        weight
    )
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        addRow("1", "1", true)
//        addRow("1iuhsiusahuioasNAsisa", "iuhsadiubdsauidsa", false)

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

    private fun addRow(link: String, name: String, downloaded: Boolean) {
        val background = if (table.childCount % 2 == 1) R.drawable.content_cell_even else R.drawable.content_cell_odd
        val newRow = TableRow(applicationContext)
        val linkView = TextView(applicationContext).apply {
            text = link
            layoutParams = layoutParam(2.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0,0)
        }
        val nameView = TextView(applicationContext).apply {
            text = name
            layoutParams = layoutParam(3.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0,0)
        }
        val buttonView = TextView(applicationContext).apply {
            text = if (downloaded) "Yes" else "No"
            layoutParams = layoutParam(1.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0,0)
        }
        newRow.addView(linkView)
        newRow.addView(nameView)
        newRow.addView(buttonView)
        table.addView(newRow)
    }
}
