package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.ui.DetailActivity

val NOTIFICATION_ID = 11
val DOWNLOAD_FILENAME = "filename"
val DOWNLOAD_STATUS = "status"
/**
 * Created By Varsha Kulkarni on 23/12/20
 */
fun NotificationManager.sendNotification(applicationContext: Context, bundle: Bundle){
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    contentIntent.putExtras(bundle)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    ).setSmallIcon(R.drawable.ic_baseline_cloud_download_24).setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.downloaded))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_baseline_cloud_download_24, applicationContext.getString(R.string.check_status), contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}
