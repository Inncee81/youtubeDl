package com.njacquemin.youtubedl

import android.app.AlertDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_link_name.view.*

private fun layoutParam(weight: Float): TableRow.LayoutParams {
    return TableRow.LayoutParams(
        0,
        TableRow.LayoutParams.MATCH_PARENT,
        weight
    )
}

class MainActivity : AppCompatActivity() {

    private class Download(var link: String, var name: String, var downloaded: Boolean)

    private var downloads: MutableList<Download> = ArrayList()

    private lateinit var youtube: YoutubeDownloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadDownloads()

        youtube = YoutubeDownloader(this, null)


        //youtube.download("https://www.youtube.com/watch?v=BaW_jenozKc", File("sdcard", "test.mp3"))

        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND == intent.action
            && intent.type != null && "text/plain" == intent.type
        ) {
            val ytLink = intent.getStringExtra(Intent.EXTRA_TEXT)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_link_name, null) as View
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(getString(R.string.set_name))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    val name = dialogView.link_name.text.toString()
                    downloads.add(Download(ytLink, name, false))
                    redrawTable()
                }.create().show()
        }
    }

    override fun onResume(){
        super.onResume()
        redrawTable()
    }

    override fun onDestroy() {
        saveDownloads()
        super.onDestroy()
    }

    private fun loadDownloads(){
        // TODO load the downloads stored in
    }

    private fun saveDownloads(){
        // TODO save the downloads stored in
    }

    private fun redrawTable(){
        val header = table.getChildAt(0)
        table.removeAllViews()
        table.addView(header)
        for (dl in downloads){
            addRow(dl)
        }
    }

    private fun addRow(dl: Download) {
        val background = if (table.childCount % 2 == 1) R.drawable.content_cell_even else R.drawable.content_cell_odd
        val newRow = TableRow(applicationContext)
        val linkView = TextView(applicationContext).apply {
            text = dl.link
            layoutParams = layoutParam(2.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0,0)
        }
        val nameView = TextView(applicationContext).apply {
            text = dl.name
            layoutParams = layoutParam(3.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0,0)
        }
        val buttonView = TextView(applicationContext).apply {
            text = if (dl.downloaded) "Yes" else "No"
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
