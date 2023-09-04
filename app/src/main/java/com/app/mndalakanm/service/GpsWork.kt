package com.app.mndalakanm.service

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.work.*
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.ProjectUtil
import com.app.mndalakanm.utils.ProjectUtil.Companion.getCurrentDate
import com.app.mndalakanm.utils.ProjectUtil.Companion.getCurrentTime
import com.app.mndalakanm.utils.SharedPref
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit


class GpsWork(context: Context, params: WorkerParameters) : Worker(context, params) {
    lateinit var sharedPref: SharedPref
    lateinit var gpsTracker: GPSTracker
    var count = 0
    override fun doWork(): Result {
        sharedPref = SharedPref(applicationContext)
        try {
        gpsTracker = GPSTracker(applicationContext)
        }
        catch (e:Exception){

        }        //Thread.sleep(20000)

        if (sharedPref.getStringValue("TIME").toString() == "") {
            sendLetLong(sharedPref, applicationContext)

        } else {
            var timenow = getCurrentTime()?.toInt()
            var timefutuer = timenow!! + 10
            var timelast = sharedPref.getStringValue("TIME")?.toInt()
            Log.e("TAG", "doWork:timenow -   " + timenow)
            Log.e("TAG", "doWork:timefutuer -   " + timefutuer)
            Log.e("TAG", "doWork:timelast -   " + timelast)


            if (timelast != null && timelast.toString() != "") {
                if (timenow - timelast > 10) {
                    sendLetLong(sharedPref, applicationContext)
                    Log.e("TAG", "doWork:timelast - timelast <= timefutuer  ")
                } else {

                    Log.e("TAG", "doWork:timelast - timelast not timefutuer  ")

                }
            } else {
                sendLetLong(sharedPref, applicationContext)

            }
        }
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
            .keepResultsForAtLeast(25, TimeUnit.MINUTES)
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
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["lat"] = sharedPref.getStringValue(Constant.LATITUDE).toString()
        map["lon"] = sharedPref.getStringValue(Constant.LONGITUDE).toString()
        map["date"] = getCurrentDate().toString()
        //   map["address"]=ProjectUtil.getCompleteAddressString(context)
        map["address"] = ProjectUtil.getCompleteAddressString(
            context,
            sharedPref.getStringValue(Constant.LATITUDE).toString(),
            sharedPref.getStringValue(Constant.LONGITUDE).toString()
        )
        Timber.tag("ContentValues.TAG").e("sendLetLongsendLetLongsendLetLong" + map)
        Mndalakanm.loadInterface()?.update_chlid_lat_lon(map)?.enqueue(object :
            Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {

                    var timenow = getCurrentTime()?.toInt()
                    var timefutuer = timenow!! + 10
                    if (timenow >= 48) {
                        sharedPref.setStringValue("TIME", "01")
                    } else {
                        sharedPref.setStringValue("TIME", getCurrentTime().toString())
                    }

                    Log.e(
                        "TAG",
                        "this is where we draw line ----: " + sharedPref.getStringValue("TIME")
                    )
                    //        Timber.tag("Exception").e("Exception = %s", response.body().toString())
                    // Thread.sleep(180000)
                    //  scheduleRecurringFetchWeatherSyncUsingWorker()
                } catch (e: Exception) {
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }


}