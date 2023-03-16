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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.app.mndalakanm.notification.Config
import com.app.mndalakanm.notification.NotifyUserReceiver
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.service.GPSTracker
import com.app.mndalakanm.service.GpsWork
import com.app.mndalakanm.utils.CrashReportingTree
import com.app.mndalakanm.utils.InternetConnection
import com.app.mndalakanm.utils.SharedPref
import com.google.firebase.database.*
import com.techno.mndalakanm.BuildConfig
import com.techno.mndalakanm.R
import com.vilborgtower.user.utils.Constant
import com.vilborgtower.user.utils.Utils
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

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        // createNotificationChannel()
        //  notificationManager.notify(1, createNotification())
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
        manager = SharedPref(applicationContext)
        utils = Utils(applicationContext)
        gpsTracker = GPSTracker(applicationContext)
        context = applicationContext
         database = FirebaseDatabase.getInstance()
         myRef = database.reference
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //to disable dark mode
        if (!InternetConnection.checkConnection(applicationContext)) {
            showToast(this, "No Internet")
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            NotifyUserReceiver(),
            IntentFilter(Config.GET_DATA_NOTIFICATION)
        )
        getLockdownOnOff()
    }

    fun scheduleRecurringFetchWeatherSyncUsingWorker() {
        val workInstance = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val data = workDataOf("WORK_DATA" to "YOUR MESSAGE.")
        val workRequest = PeriodicWorkRequestBuilder<GpsWork>(10, TimeUnit.MINUTES)
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
        val intent = Intent(Config.GET_DATA_LOCKDOWN)
        intent.putExtra("pushNotificationModel", "1")
        intent.putExtra("staus", "1")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                contentIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val fullScreenIntent = Intent(this, LockscreenActivity::class.java)
        val fullScreenPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setContentTitle("Heads Up Notification")
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
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
