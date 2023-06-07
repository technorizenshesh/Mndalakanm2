package com.app.mndalakanm.utils

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent


/**
 * DeviceAdminDemo to enable, disable the options.
 * @author Prashant Adesara
 */
class DeviceAdmin : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {

    }

    override fun onDisabled(context: Context, intent: Intent) {

    }
}

