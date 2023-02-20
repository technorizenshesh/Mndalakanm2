package com.app.mndalakanm.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager

class OverlayView(context: Context) : View(context) {
    // Implement custom drawing on the overlay view

}

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: OverlayView

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = OverlayView(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overlayView)
    }

    override fun onBind(intent: Intent?) = null
}