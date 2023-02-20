package com.app.mndalakanm.utils

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.Window
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ScreenshotServicektt : Service() {

    private var timer: Timer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                takeScreenshot()
            }
        }, 0, 10_000)
    }

    private fun takeScreenshot() {
        val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val path = Environment.getExternalStorageDirectory().toString() + "/screenshots/"
        val file = File(path)

        if (!file.exists()) {
            file.mkdirs()
        }
        val mPath = "$path$date.jpeg"
        val view = (application as Window).decorView.rootView
        val bitmap = getScreenShotFromView(view)
        try {
            val output = FileOutputStream(mPath)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, output)
            if (bitmap != null) {
                Timber.tag("TAG").e("takeScreenshot: " + bitmap.height)
                Timber.tag("TAG").e("takeScreenshot: " + bitmap.toString())
            }
            output.flush()
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }
}
