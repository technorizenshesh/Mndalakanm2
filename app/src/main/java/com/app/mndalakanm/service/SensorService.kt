package com.app.mndalakanm.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import java.util.*


/**
 * Created by fabio on 30/01/2016.
 */
class SensorService : Service {
    var counter = 0

    constructor(applicationContext: Context?) : super() {
        Log.e("HERE", "here I am!")
    }

    constructor() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("EXIT", "ondestroy!")
        val broadcastIntent = Intent(this, SensorRestarterBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var oldTime: Long = 0
    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                Log.e("in timer", "in timer ++++  " + counter++)
            }
        }
    }

    /**
     * not needed
     */
    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}