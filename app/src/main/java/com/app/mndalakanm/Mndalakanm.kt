package com.app.mndalakanm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.utils.InternetConnection
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.app.mndalakanm.service.GPSTracker
import com.app.mndalakanm.service.GpsWork
import com.app.mndalakanm.utils.CrashReportingTree
import com.techno.mndalakanm.BuildConfig
import com.vilborgtower.user.utils.Utils
import timber.log.Timber

class Mndalakanm :Application(){
    var manager: SharedPref? = null
    var mWorkManager: WorkManager? = null
    var utils: Utils? = null
    var gpsTracker: GPSTracker? = null
    val TAG_WORK_NAME = "newcode"

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
      //  MultiDex.install(this)
    }
    override   fun  onCreate() {
        //MultiDex.install(applicationContext)
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
        //Timber.plant(Timber.DebugTree())
        manager = SharedPref(applicationContext)
        utils = Utils(applicationContext)
        gpsTracker = GPSTracker(applicationContext)
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //to disable dark mode
        if (!InternetConnection.checkConnection(applicationContext)) {
            showToast(this,"No Internet")
        }
       // scheduleRecurringFetchWeatherSyncUsingWorker()
    }
    fun scheduleRecurringFetchWeatherSyncUsingWorker() {
         val workInstance = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val data = workDataOf("WORK_DATA" to "YOUR MESSAGE.")

           val workRequest = OneTimeWorkRequestBuilder<GpsWork>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
      /*  val workRequest = PeriodicWorkRequestBuilder<GpsWork>(2, TimeUnit.SECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()*/
        workInstance.enqueue(workRequest)
    }
    companion object {
        var apiInterface: ProviderInterface? = null
        @SuppressLint("StaticFieldLeak")
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
