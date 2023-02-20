package com.app.mndalakanm.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.mndalakanm.model.PushNotificationModel


class NotifyUserReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Config.GET_DATA_NOTIFICATION) {
            val pushNotificationModel =
                intent.getSerializableExtra("pushNotificationModel") as PushNotificationModel?
        }
    }
}