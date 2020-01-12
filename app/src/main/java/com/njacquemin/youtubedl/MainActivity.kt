package com.njacquemin.youtubedl

import android.Manifest
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_link_name.view.*
import kotlinx.android.synthetic.main.dialog_add_link.view.*
import java.io.File
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.google.gson.reflect.TypeToken

private const val YT_DOWNLOAD_REQUEST_CODE = 0

private val PERMISSIONS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

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



    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        loadDownloads()

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

        add_button.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_link, null) as View
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(getString(R.string.add_link))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    val ytLink = dialogView.link.text.toString()
                    val name = dialogView.name.text.toString()
                    downloads.add(Download(ytLink, name, false))
                    redrawTable()
                }.create().show()
        }
    }

    override fun onResume() {
        super.onResume()

        if (checkPermission()){
            redrawTable()
        } else {
            requestPermissions(PERMISSIONS, 0)
        }
    }

    private fun checkPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermission()){
            redrawTable()
        } else {
            requestPermissions(PERMISSIONS, 0)
        }
    }

    override fun onPause() {
        saveDownloads()
        super.onPause()
    }

    private fun loadDownloads() {
        Gson().fromJson<MutableList<Download>>(
            sharedPrefs.getString("SAVED_DOWNLOADS", ""),
            object : TypeToken<MutableList<Download>>() {
            }.type
        )?.let {downloads = it}
    }

    private fun saveDownloads() {
        sharedPrefs.edit().apply {
            putString("SAVED_DOWNLOADS", Gson().toJson(downloads))
            apply()
        }
    }

    private fun redrawTable() {
        val header = table.getChildAt(0)
        table.removeAllViews()
        table.addView(header)
        for (dl in downloads) {
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
            setPadding(10, 0, 0, 0)
        }
        val nameView = TextView(applicationContext).apply {
            text = dl.name
            layoutParams = layoutParam(3.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0, 0)
        }
        val buttonView = ImageButton(applicationContext).apply {
            setImageResource(if (dl.downloaded) R.drawable.ic_delete_forever_black_24dp else R.drawable.ic_file_download_black_24dp)
            layoutParams = layoutParam(1.0f)
            setBackgroundResource(background)
            setPadding(10, 0, 0, 0)
            setOnClickListener {
                if (dl.downloaded) {
                    // Remove
                    var idxToRemove = -1
                    downloads.forEachIndexed { index, download ->
                        if (dl == download) {
                            idxToRemove = index
                        }
                    }
                    if (idxToRemove != -1) {
                        downloads.removeAt(idxToRemove)
                    }
                    redrawTable()
                } else {
                    setImageResource(R.drawable.ic_access_time_black_24dp)
                    isEnabled = false
                    // Launch download

                    val pendingResult = createPendingResult(
                        YT_DOWNLOAD_REQUEST_CODE, Intent(), 0
                    )
                    val intent = Intent(applicationContext, YoutubeDlService::class.java)
                    intent.putExtra(LINK, dl.link)
                    intent.putExtra(FILEPATH,File("sdcard", dl.name + ".mp3").absolutePath)
                    intent.putExtra(PENDING_RESULT_EXTRA, pendingResult)
                    startService(intent)
                }
            }
        }
        newRow.addView(linkView)
        newRow.addView(nameView)
        newRow.addView(buttonView)
        table.addView(newRow)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == YT_DOWNLOAD_REQUEST_CODE) {
            val link = data!!.getStringExtra(LINK)
            downloads.find {
                candidate -> candidate.link == link
            }?.downloaded = true
            redrawTable()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
