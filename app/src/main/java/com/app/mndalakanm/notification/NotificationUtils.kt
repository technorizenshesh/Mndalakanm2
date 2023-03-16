package com.app.mndalakanm.notification

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Patterns
import androidx.core.app.NotificationCompat
import com.app.mndalakanm.MainActivity
import com.app.mndalakanm.model.PushNotificationModel
import com.techno.mndalakanm.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NotificationUtils(private val mContext: Context) {
    //  private lateinit var mediaPlayer: MediaPlayer

    fun showNotificationMessage(pushNotificationModel: PushNotificationModel.Message) {
        // Check for empty push message
//        if (TextUtils.isEmpty(pushNotificationModel.job_id)) return
        try {
            val intent: Intent
            //            if (pushNotificationModel.getType().equals("chat")) {
            intent = Intent(mContext, MainActivity::class.java)
            //            } else {
//                intent = new Intent(mContext, NotificationActivity.class);
//            }
            intent.putExtra("pushNotificationModel", pushNotificationModel)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val flag =
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val resultPendingIntent =
                PendingIntent.getActivity(
                    mContext,
                    0,
                    intent,
                    flag
                )
            val mBuilder = NotificationCompat.Builder(
                mContext
            )
            val alarmSound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + mContext.packageName + "/raw/doogee_ringtone.mp3"
            )
            //
////            if (!TextUtils.isEmpty(pushNotificationModel.getImage())) {
//            if(isImageAvailable(pushNotificationModel.getImage())){
//
//                if (pushNotificationModel.getImage() != null && pushNotificationModel.getImage().length() > 4 && Patterns.WEB_URL.matcher(pushNotificationModel.getImage()).matches()) {
//
////                    Bitmap bitmap = getBitmapFromURL(pushNotificationModel.getImage());
////
////                    if (bitmap != null&&!bitmap.equals("")) {
//                        //showBigNotification(/*bitmap,*/ mBuilder, pushNotificationModel, resultPendingIntent, alarmSound);
//                        showSmallNotification(mBuilder, pushNotificationModel, resultPendingIntent, alarmSound);
//
//                    } else {
//                        showSmallNotification(mBuilder, pushNotificationModel, resultPendingIntent, alarmSound);
////                    }
//                }
//            } else {
            showSmallNotification(mBuilder, pushNotificationModel, resultPendingIntent, alarmSound)
            //            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSmallNotification(
        mBuilder: NotificationCompat.Builder,
        pushNotificationModel: PushNotificationModel.Message,
        resultPendingIntent: PendingIntent,
        alarmSound: Uri
    ) {
        var imageUrl = ""
        if (pushNotificationModel.parentImage != null) {
            if (isImageAvailable(pushNotificationModel.parentImage!!)) {
                imageUrl = pushNotificationModel.parentImage!!
            } else {
                imageUrl = ""
            }
        }
        //        int icon=R.drawable.ic_launcher_background;
        val title =
            pushNotificationModel.parentName //pushNotificationModel.getKey().equals("")?mContext.getString(R.string.app_name):pushNotificationModel.getKey();
        val message = pushNotificationModel.key //pushNotificationModel.getMessage();

        // mediaPlayer = MediaPlayer.create(mContext,R.raw.doogee_ringtone)
        // mediaPlayer.start()

//        playNotificationSound();
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.addLine(message);
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(message)
        val NOTIFICATION_CHANNEL_ID = "1"
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "My Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Configure the notification channel.
        notificationChannel.description = "Channel description"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
        val builder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
            .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_launcher_background /*icon*/).setTicker(title).setWhen(0)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL) //                .setStyle(inboxStyle)
            .setStyle(bigTextStyle)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setWhen(Calendar.getInstance().timeInMillis) //timeStamp))
            .setContentIntent(resultPendingIntent) //                .addAction(android.R.drawable.ic_delete, "Ignore", resultPendingIntent)
            //                .addAction(android.R.drawable.ic_media_next, "Agree", resultPendingIntent)
            .setLargeIcon((getBitmapFromURL(imageUrl)))//BitmapFactory.decodeResource(mContext.getResources(), icon)))
            .setContentText(message)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun showBigNotification( /*Bitmap bitmap,*/
        mBuilder: NotificationCompat.Builder,
        pushNotificationModel: PushNotificationModel.Message,
        resultPendingIntent: PendingIntent,
        alarmSound: Uri
    ) {
        val imageUrl = "" //pushNotificationModel.getImage();
        //        int icon=R.drawable.ic_launcher_background;
        val title =
            if (pushNotificationModel.key == "") mContext.getString(R.string.app_name) else pushNotificationModel.key!!
        val message = pushNotificationModel.key

//        playNotificationSound();
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        bigPictureStyle.bigPicture(getBitmapFromURL(imageUrl)) //bitmap);
        val notification: Notification
        notification =
            mBuilder.setSmallIcon(R.drawable.ic_launcher_background).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent) //                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSound(alarmSound)
                // .setWhen(getTimeMilliSec(pushNotificationModel.req_datetime)) //timeStamp))
                .setWhen(Calendar.getInstance().timeInMillis) //timeStamp))
                .setStyle(bigPictureStyle) //                .setSmallIcon(icon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        mContext.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentText(message)
                .build()
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification)
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    fun getBitmapFromURL(strURL: String?): Bitmap? {
        try {

            if (strURL.equals("")) {
                return BitmapFactory.decodeResource(
                    mContext.resources,
                    R.drawable.logo_inside
                )
            } else {
                val url = URL(strURL)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            return BitmapFactory.decodeResource(
                mContext.resources,
                R.drawable.logo_inside
            )
        }
    }

    // Playing notification sound
    fun playNotificationSound() {
        try {
            val alarmSound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.packageName + "/raw/notification")
            val r = RingtoneManager.getRingtone(mContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isImageAvailable(image: String): Boolean {
        var vl = false
        if (image == "null") {
            vl = false
        } else if (image == "") {
            vl = false
        } else if (Patterns.WEB_URL.matcher(image).matches()) {
            vl = true
        }
        return vl
    }

    companion object {
        private val TAG = NotificationUtils::class.java.simpleName
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }

        // Clears notification tray messages
        fun clearNotifications(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }

        fun getTimeMilliSec(timeStamp: String?): Long {
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            try {
                val date = format.parse(timeStamp)
                if (date != null) {
                    return date.time
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
    }
}