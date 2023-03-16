package com.app.mndalakanm.notification

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.mndalakanm.model.PushNotificationModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber
import kotlin.math.log

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
        if (remoteMessage.notification != null) {
            Timber.tag(TAG).e("Notification Body: %s", remoteMessage.notification!!.body)
            val pushNotificationModel = PushNotificationModel.Message()
            pushNotificationModel.parentName = (remoteMessage.notification!!.title)
            pushNotificationModel.key = (remoteMessage.notification!!.body)
            pushNotificationModel.childName = remoteMessage.notification!!.imageUrl?.toString()
            //pushNotificationModel.time_stamp = (""/*Utils.currentDate*/)
            handleNotification(pushNotificationModel)
        }
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Timber.tag(TAG).e("Data Payload: %s", remoteMessage.data.toString())
            try {
                val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)
//                    val data = remoteMessage.data
//                    val key1= data["message"]
//                    val jsonObject = JSONObject(key1 as Map<*, *>)
                val pushNotificationModel: PushNotificationModel.Message =
                    Gson().fromJson(
                        jsonObject.toString(),
                        PushNotificationModel.Message::class.java
                    )
                Log.e(TAG, "onMessageReceived: "+ "21222342141343432"+jsonObject.toString())
                Log.e(TAG, "onMessageReceived: "+ "21222342141343432"+pushNotificationModel.toString())
                Log.e(TAG, "onMessageReceived: "+ "21222342141343432"+pushNotificationModel.key.toString())

                if (pushNotificationModel.key.toString().equals("Child divice is connected", true)) {
                    Log.e(TAG, "onMessageReceived: "+ "21222342141343432jjh")
                    val intent2 = Intent("ChildConnected")
                    //intent2.putExtra("pushNotificationModel", pushNotificationModel)
                   // intent2.putExtra("type", pushNotificationModel.type)
                   sendBroadcast(intent2)

                } else {

                    val intent = Intent(Config.GET_DATA_NOTIFICATION)
                    intent.putExtra("pushNotificationModel", pushNotificationModel)
                    intent.putExtra("type", pushNotificationModel.type)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    handleDataMessage(pushNotificationModel)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e("Exception: %s", e.message)
            }
        }
    }

    private fun handleNotification(pushNotificationModel: PushNotificationModel.Message) {
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
    }

    private fun handleDataMessage(pushNotificationModel: PushNotificationModel.Message) {
        try {
            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("pushNotificationModel", pushNotificationModel)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                showNotificationMessage(applicationContext, pushNotificationModel)
            } else {
                showNotificationMessage(applicationContext, pushNotificationModel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
    }

    private fun showNotificationMessage(
        context: Context,
        pushNotificationModel: PushNotificationModel.Message
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils!!.showNotificationMessage(pushNotificationModel)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}