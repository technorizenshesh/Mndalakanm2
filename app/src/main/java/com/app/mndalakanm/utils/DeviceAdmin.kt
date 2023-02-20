package com.app.mndalakanm.utils

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent


/**
 * DeviceAdminDemo to enable, disable the options.
 * @author Prashant Adesara
 */
class DeviceAdmin : DeviceAdminReceiver() {
    //	implement onEnabled(), onDisabled(),
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {

    }
    override fun onDisabled(context: Context, intent: Intent) {

    }
}

