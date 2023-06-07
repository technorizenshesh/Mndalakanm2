package com.app.mndalakanm.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.app.ActivityCompat.startActivityForResult
import com.app.mndalakanm.ui.Home.SuperviseHomeActivity
import com.app.mndalakanm.utils.SharedPref
import com.mtsahakis.mediaprojectiondemo.MainService
import timber.log.Timber


class NotifyUserReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Config.GET_DATA_NOTIFICATION) {
            Timber.tag("TAG").e("onReceive:GET_DATA_NOTIFICATION ")

            //  val pushNotificationModel = intent.getSerializableExtra("pushNotificationModel")
            //       as PushNotificationModel?
            SharedPref(context!!).setStringValue("Screenshot", "true")
            val intent = Intent(context, SuperviseHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }else
            if (intent.action == Config.GET_DATA_LOCKDOWN)
            {
            Log.e("TAG", "onReceive: GET_DATA_LOCKDOWN" )

            Timber.tag("TAG").e("onReceive:GET_DATA_LOCKDOWN ")
            val pushNotificationModel = intent.getSerializableExtra("pushNotificationModel")
             try {

                 val status = intent.getSerializableExtra("status")

                 Log.e("TAG", "onReceive: GET_DATA_LOCKDOWN  status " +status)

                 val svc = Intent( context, MainService::class.java)

            if( status =="1"){
                context!!.stopService(svc)
                context.startService(svc)
             }else{
                context!!.stopService(svc)

            }


        }catch (e:Exception){
                 Log.e("TAG", "onReceive: "+e.message )
                 Log.e("TAG", "onReceive: "+e.localizedMessage )
            e.printStackTrace()}
        }
    }
}