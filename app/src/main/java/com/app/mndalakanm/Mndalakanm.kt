package com.app.mndalakanm

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.utils.InternetConnection
import com.app.mndalakanm.utils.MyService
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.app.mndalakanm.service.GPSTracker
import com.vilborgtower.user.utils.Utils
import timber.log.Timber

class Mndalakanm :Application(){
    var manager: SharedPref? = null
    var utils: Utils? = null
    var gpsTracker: GPSTracker? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
      //  MultiDex.install(this)
    }
    override fun onCreate() {
        //MultiDex.install(applicationContext)
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        manager = SharedPref(applicationContext)
        utils = Utils(applicationContext)
        gpsTracker = GPSTracker(applicationContext)
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //to disable dark mode
        startService(Intent(applicationContext, MyService::class.java))
        if (!InternetConnection.checkConnection(applicationContext)) {
            showToast(this,"No Internet")
        }
    }

    companion object {
        var apiInterface: ProviderInterface? = null
        var context: Context? = null

        fun loadInterface(): ProviderInterface? {
            if (apiInterface == null) {
                apiInterface = ApiClient.getClient(context!!)?.create(ProviderInterface::class.java)
            }
            return apiInterface
        }



        fun showConnectionDialog(mContext: Context) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(mContext.getString(R.string.please_check_internet))
                .setCancelable(false)
                .setPositiveButton(
                    mContext.getString(R.string.ok)
                ) { dialog, which -> dialog.dismiss() }.create().show()
        }

        fun showAlert(mContext: Context, text: String) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(text).setCancelable(false).setPositiveButton(
                mContext.getString(R.string.ok)
            ) { dialog, which -> dialog.dismiss() }.create().show()
        }

        fun showToast(mContext: Context?, msg: String) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
        }

        fun get(): Mndalakanm? {
            return get()
        }

    }
}
