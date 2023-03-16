package com.app.mndalakanm.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.mndalakanm.model.PushNotificationModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var notificationUtils: NotificationUtils? = null

    override fun onCreate() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            NotifyUserReceiver(),
            IntentFilter(Config.GET_DATA_NOTIFICATION)
        )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload.
        /* if (remoteMessage.notification != null) {
             Timber.tag(TAG).e("Notification Body: %s", remoteMessage.notification!!.body)
             val pushNotificationModel = PushNotificationModel()
             pushNotificationModel.parentName = (remoteMessage.notification!!.title)
             pushNotificationModel.key = (remoteMessage.notification!!.body)
             pushNotificationModel.childName = remoteMessage.notification!!.imageUrl?.toString()
             handleNotification(pushNotificationModel)
         }*/
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Timber.tag(TAG).e("Data Payload: %s", remoteMessage.data.toString())
            try {
                val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)
//                    val data = remoteMessage.data
//                    val key1= data["message"]
//                    val jsonObject = JSONObject(key1 as Map<*, *>)
                val pushNotificationModel: PushNotificationModel = Gson().fromJson(
                    jsonObject.toString(),
                    PushNotificationModel::class.java
                )
                Timber.tag(TAG)
                    .e("%s%s", "onMessageReceived: " + "jsonObject", jsonObject.toString())
                Timber.tag(TAG)
                    .e("%s%s", "onMessageReceived: " + "pushNotificationModel", pushNotificationModel.toString())
                Timber.tag(TAG).e("%s%s", "onMessageReceived: " + "pushNotificationModelled", pushNotificationModel.key.toString())

                if (pushNotificationModel.key.toString()
                        .equals("Child divice is connected", true)
                ) {
                    Timber.tag(TAG).e("ChildChildChild: " + "21222342141343432jjh")
                    val intent2 = Intent("ChildConnected")
                    //intent2.putExtra("pushNotificationModel", pushNotificationModel)
                    // intent2.putExtra("type", pushNotificationModel.type)
                    sendBroadcast(intent2)
                } else if (pushNotificationModel.key.toString()
                        .equals("Your time successfully added", true)
                ) {
                    Timber.tag(TAG).e("timetimetime: " + "TimeAddedTimeAdded")
                    val intent2 = Intent("TimeAdded")
                    intent2.putExtra("pushNotificationModel", "1")
                    // intent2.putExtra("type", pushNotificationModel.type)
                    sendBroadcast(intent2)
                } else if (pushNotificationModel.key.toString()
                        .equals(" want to take screenshot", true)
                ) {
                    Timber.tag(TAG).e("screenshotscreenshot: " + "screenshotscreenshot")
                    val intent23 = Intent("TimeAdded")
                    intent23.putExtra("pushNotificationModel", "2")
                    // intent2.putExtra("type", pushNotificationModel.type)
                    sendBroadcast(intent23)
                    //  HomeFragment.startProjection(applicationContext)

                }


                handleDataMessage(pushNotificationModel)

            } catch (e: Exception) {
                Timber.tag(TAG).e("Exception: %s", e.message)
            }
        }
    }

  /*  private fun handleNotification(pushNotificationModel: PushNotificationModel) {
        if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            // app is in foreground, broadcast the push message
            val pushNotification = Intent(Config.PUSH_NOTIFICATION)
            pushNotification.putExtra("pushNotificationModel", pushNotificationModel)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            // play notification sound
            showNotificationMessage(applicationContext, pushNotificationModel)
        } else {

            showNotificationMessage(applicationContext, pushNotificationModel)
            // If the app is in background, firebase itself handles the notification
        }
    }*/

    private fun handleDataMessage(pushNotificationModel: PushNotificationModel) {
        try {
            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("pushNotificationModel", pushNotificationModel)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                showNotificationMessage(applicationContext, pushNotificationModel)


            } else {
                val intent = Intent(Config.GET_DATA_NOTIFICATION)
                intent.putExtra("pushNotificationModel", pushNotificationModel)
                intent.putExtra("type", pushNotificationModel.type)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                showNotificationMessage(applicationContext, pushNotificationModel)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e("Exception: %s", e.message)
        }
    }

    private fun showNotificationMessage(
        context: Context,
        pushNotificationModel: PushNotificationModel
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils!!.showNotificationMessage(pushNotificationModel)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}