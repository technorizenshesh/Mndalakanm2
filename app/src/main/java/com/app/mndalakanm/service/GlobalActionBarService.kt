package com.app.mndalakanm.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.techno.mndalakanm.R
import java.util.*
import kotlin.collections.ArrayDeque


class GlobalActionBarService : AccessibilityService() {
    var mLayout: FrameLayout? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        // Create an overlay and display the action bar
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)
        configurePowerButton()
        configureVolumeButton()
        configureScrollButton()
        configureSwipeButton()
    }

    private fun configurePowerButton() {
        val powerButton: Button = mLayout!!.findViewById<View>(R.id.power) as Button
        powerButton.setOnClickListener {
            performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        }
    }

    private fun configureVolumeButton() {
        val volumeUpButton = mLayout!!.findViewById<View>(R.id.volume_up) as Button
        volumeUpButton.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
            )
        }
    }

    private fun findScrollableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val deque: ArrayDeque<AccessibilityNodeInfo> = ArrayDeque()
        deque.add(root)
        while (!deque.isEmpty()) {
            val node: AccessibilityNodeInfo = deque.removeFirst()
            if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                return node
            }
            for (i in 0 until node.childCount) {
                deque.addLast(node.getChild(i))
            }
        }
        return null
    }

    private fun configureScrollButton() {
        val scrollButton = mLayout!!.findViewById<View>(R.id.scroll) as Button
        scrollButton.setOnClickListener {
            val scrollable = findScrollableNode(rootInActiveWindow)
            scrollable?.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun configureSwipeButton() {
        val swipeButton = mLayout!!.findViewById<View>(R.id.swipe) as Button
        swipeButton.setOnClickListener {
            val swipePath = Path()
            swipePath.moveTo(1000F, 1000F)
            swipePath.lineTo(100F, 1000F)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 500))
            dispatchGesture(gestureBuilder.build(), null, null)
        }
    }
}