package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.util.DOWNLOAD_FILENAME
import com.udacity.util.DOWNLOAD_STATUS
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
        )

        setupUI()

    }

    override fun onResume() {
        super.onResume()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(receiver)
    }

    private fun setupUI() {

        custom_button.setOnClickListener {
            when (rg_load_options.checkedRadioButtonId) {
                R.id.rb_glide -> {
                    download(URL_GLIDE, getString(R.string.glide_option))
                    custom_button.setLoadingButtonState(ButtonState.Loading)
                }

                R.id.rb_udacity -> {
                    download(URL_LOADAPP, getString(R.string.loadapp_option))
                    custom_button.setLoadingButtonState(ButtonState.Loading)
                }

                R.id.rb_retrofit -> {
                    download(URL_RETROFIT, getString(R.string.retrofit_option))
                    custom_button.setLoadingButtonState(ButtonState.Loading)
                }

                else -> {
                    Toast.makeText(applicationContext, R.string.select_download_file, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            custom_button.setLoadingButtonState(ButtonState.Completed)

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val notificationManager = getSystemService(NotificationManager::class.java)

            val query = id?.let { DownloadManager.Query().setFilterById(it) }
            val cursor = downloadManager.query(query)
            if(cursor.moveToFirst()){
                val filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val bundle = Bundle()
                bundle.putString(DOWNLOAD_FILENAME, filename)
                bundle.putInt(DOWNLOAD_STATUS, status)

                notificationManager.cancelAll()
                notificationManager.sendNotification(applicationContext, bundle)
            }
        }
    }

    private fun download(url: String, title: String) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(title)
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_LOW)
                            .apply {
                                setShowBadge(false)
                            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val  URL_GLIDE =  "https://github.com/bumptech/glide/archieve/master.zip"
        private const val URL_LOADAPP =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
