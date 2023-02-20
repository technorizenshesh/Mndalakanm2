package com.app.mndalakanm.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.mndalakanm.notification.NotificationUtils
import com.techno.mndalakanm.R


class ForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        throw  UnsupportedOperationException("Not yet implemented");

    }
    override fun onCreate() {
        super.onCreate()
        Log.e("TAG", "onCreate: ForegroundServiceForegroundServiceForegroundService", )
        // create the custom or default notification
        // based on the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground()
        else startForeground(1, Notification())

        // create an instance of Window class
        // and display the content on screen
       // OverlayView(this)
        val window = Window()
        window.getWindow(this)
        window.open()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "1"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
/*
        val builder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
            .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_launcher_background *//*icon*//*).setTicker(title).setWhen(0)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL) //                .setStyle(inboxStyle)
            .setStyle(bigTextStyle)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setWhen(NotificationUtils.getTimeMilliSec(pushNotificationModel.req_datetime)) //timeStamp))
            .setContentIntent(resultPendingIntent) //                .addAction(android.R.drawable.ic_delete, "Ignore", resultPendingIntent)
            //                .addAction(android.R.drawable.ic_media_next, "Agree", resultPendingIntent)
            .setLargeIcon((getBitmapFromURL(imageUrl)))//BitmapFactory.decodeResource(mContext.getResources(), icon)))
            .setContentText(message)
        */

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Service running")
            .setContentText("Displaying over other apps") // this is important, otherwise the notification will show the way
            // you want i.e. it will show some default notification
            .setSmallIcon(R.drawable.logo)
            .setPriority(Notification.PRIORITY_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
        OverlayView(this)
    }

}