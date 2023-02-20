package com.app.mndalakanm

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.app.mndalakanm.notification.Config
import com.app.mndalakanm.notification.NotifyUserReceiver
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.service.GPSTracker
import com.app.mndalakanm.service.GpsWork
import com.app.mndalakanm.service.SensorService
import com.app.mndalakanm.utils.*
import com.google.firebase.database.*
import com.mtsahakis.mediaprojectiondemo.SharedPreferenceUtility
import com.mtsahakis.mediaprojectiondemo.SharedPreferenceUtility.*
import com.vilborgtower.user.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val CHANNEL_ID = "heads_up_alerts"
private const val CHANNEL_NAME = "Heads Up Alerts"

class Mndalakanm : Application() {
    var manager: SharedPref? = null
    var utils: Utils? = null
    var gpsTracker: GPSTracker? = null
    lateinit var database:FirebaseDatabase
    lateinit var myRef: DatabaseReference
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    var mServiceIntent: Intent? = null
    private var mSensorService: SensorService? = null
    var ctx: Context? = null
    @JvmName("getCtx1")
    fun getCtx(): Context? {
        return ctx
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
        manager = SharedPref(applicationContext)
        getInstance(applicationContext).putString("parent_id", manager!!.getStringValue(Constant.USER_ID).toString())
        getInstance(applicationContext).putString("child_id", manager!!.getStringValue(Constant.CHILD_ID).toString())

        utils = Utils(applicationContext)
        gpsTracker = GPSTracker(applicationContext)
        context = applicationContext
         database = FirebaseDatabase.getInstance()
         myRef = database.reference
            val myScope = CoroutineScope(Dispatchers.Default)

        myScope.launch {
            try {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //to disable dark mode
                mSensorService = SensorService(getCtx())
                mServiceIntent = Intent(getCtx(), SensorService::class.java)
                if (!isMyServiceRunning( SensorService::class.java)) {
                    startService(mServiceIntent)
                }



                if (!InternetConnection.checkConnection(applicationContext)) {
                    showToast(applicationContext, "No Internet")
                }
                LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
                    NotifyUserReceiver(),
                    IntentFilter(Config.GET_DATA_NOTIFICATION)
                )
                getLockdownOnOff()


                if (manager?.getStringValue(Constant.USER_TYPE).equals("child", true)) {
                    scheduleRecurringFetchWeatherSyncUsingWorker()
                } else {
                }

                myScope.cancel()

            }catch (e:Exception){
                myScope.cancel()

            }
        }

    }
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.e("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.e("isMyServiceRunning?", false.toString() + "")
        return false
    }

    fun scheduleRecurringFetchWeatherSyncUsingWorker() {
        val workInstance = WorkManager.getInstance(this)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
        val data = workDataOf("WORK_DATA" to "YOUR MESSAGE.")
        val workRequest = PeriodicWorkRequestBuilder<GpsWork>(5, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workInstance.enqueue(workRequest)
    }

    private fun getLockdownOnOff() {
        myRef.child("LockDown")
            .child(manager?.getStringValue(Constant.USER_ID).toString())
            .child(manager?.getStringValue(Constant.CHILD_ID).toString())
            .child("Status")
        // Attach a ValueEventListener to the "users" reference
        val usersListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called whenever the data at the "users" reference changes.
                // Use the dataSnapshot parameter to access the new data.
                Log.e(TAG, "UserUserUserUserUserUserUser: $dataSnapshot")
                Log.e(TAG, "UserUserUserUserUserUserUser: ${dataSnapshot.value}")
                if (dataSnapshot.value.toString() == "1") {
                    if (manager?.getStringValue(Constant.USER_TYPE).equals("Child", true)) {
                        live(applicationContext,dataSnapshot.value.toString())
                    }
                } else {
                    live(applicationContext,dataSnapshot.value.toString())

                }


            }

            override fun onCancelled(error: DatabaseError) {
                // This method is called if there is an error reading the data.
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }
        myRef.addValueEventListener(usersListener)
    }

    private fun live(context: Context,status:String) {
       /* val intent = Intent(Config.GET_DATA_LOCKDOWN)
        intent.putExtra("pushNotificationModel", "1")
        intent.putExtra("staus", "1")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)*/
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
