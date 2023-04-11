package com.app.mndalakanm.utils.work
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.util.AttributeSet
import android.widget.TimePicker

@Suppress("DEPRECATION")
class TimePickerCustom : TimePicker {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun setHour(hour: Int) {
        when {
            true -> super.setHour(hour)
            else -> super.setCurrentHour(hour)
        }
    }

    override fun setMinute(minute: Int) {
        when {
            true -> super.setMinute(minute)
            else -> super.setCurrentMinute(minute)
        }
    }

    override fun getHour(): Int {
        return when {
            true -> super.getHour()
            else -> super.getCurrentHour()
        }
    }

    override fun getMinute(): Int {
        return when {
            true -> super.getMinute()
            else -> super.getCurrentMinute()
        }
    }
}