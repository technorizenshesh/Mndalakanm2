package com.app.mndalakanm.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.work.*
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.utils.ProjectUtil.Companion.getCurrentDate
import com.app.mndalakanm.utils.SharedPref
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.TimeUnit


class GpsWork (context: Context, params: WorkerParameters) : Worker(context, params) {
    lateinit var sharedPref: SharedPref
    override fun doWork(): Result {
        sharedPref = SharedPref(applicationContext)
        Thread.sleep(20000)
        sendLetLong(sharedPref, applicationContext)
     //   scheduleRecurringFetchWeatherSyncUsingWorker()

        return Result.success()
    }


    @SuppressLint("RestrictedApi")
    override fun getWorkerFactory(): WorkerFactory {
        return super.getWorkerFactory()
    }
    fun scheduleRecurringFetchWeatherSyncUsingWorker() {
        val workInstance = WorkManager.getInstance(applicationContext)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val data = workDataOf("WORK_DATA" to "YOUR MESSAGE.")

        val workRequest = OneTimeWorkRequestBuilder<GpsWork>()
            .setInputData(data)
            .setConstraints(constraints)
            .keepResultsForAtLeast(5,TimeUnit.HOURS)
            .build()
        /*  val workRequest = PeriodicWorkRequestBuilder<GpsWork>(2, TimeUnit.SECONDS)
              .setInputData(data)
              .setConstraints(constraints)
              .build()*/
        workInstance.enqueue(workRequest)
    }
    @SuppressLint("SuspiciousIndentation")
    private fun sendLetLong(sharedPref: SharedPref, context: Context) {
        val map = HashMap<String, String>()
        /*parent_id=1&child_id=1&address=&lat=22.7196&lon*/
        map["parent_id"]=sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"]=sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["lat"]=sharedPref.getStringValue(Constant.LATITUDE).toString()
        map["lon"]=sharedPref.getStringValue(Constant.LONGITUDE).toString()
        map["date"]=getCurrentDate().toString()
        //   map["address"]=ProjectUtil.getCompleteAddressString(context)
        map["address"] = "demo"
        Timber.tag("ContentValues.TAG").e("sendLetLongsendLetLongsendLetLong"+ map)
        Mndalakanm.loadInterface()?.update_chlid_lat_lon(map)?.enqueue(object :
            Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
            //        Timber.tag("Exception").e("Exception = %s", response.body().toString())

                } catch (e: Exception) {
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }


}