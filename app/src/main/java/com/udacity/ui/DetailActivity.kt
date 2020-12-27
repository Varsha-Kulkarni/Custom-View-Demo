package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.util.DOWNLOAD_FILENAME
import com.udacity.util.DOWNLOAD_STATUS
import com.udacity.util.NOTIFICATION_ID
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        dismissNotification()
        updateUI()
    }

    private fun updateUI() {
        val extras = intent.extras
        extras.apply {
            val filename = this?.getString(DOWNLOAD_FILENAME)
            val status = this?.getInt(DOWNLOAD_STATUS)

            tv_filename.text = filename
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    tv_status.text = getString(R.string.success_status)
                }
                DownloadManager.STATUS_FAILED -> {
                    tv_status.text = getString(R.string.failure_status)
                    tv_status.setTextColor(getColor(R.color.colorFailed))
                }
            }

            btn_ok.setOnClickListener {
                finish()
            }
        }
    }

    private fun dismissNotification(){

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
