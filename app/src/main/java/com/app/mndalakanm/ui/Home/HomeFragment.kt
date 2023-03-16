package com.app.mndalakanm.ui.Home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mndalakanm.adapter.AdapterScreenshotList
import com.app.mndalakanm.model.SuccessChildRemainTime
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.mtsahakis.mediaprojectiondemo.ScreenCaptureService
import com.mtsahakis.mediaprojectiondemo.SharedPreferenceUtility
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentHomeBinding
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask


class HomeFragment : Fragment(), ScreenShotClickListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var mProjectionManager: MediaProjectionManager
    var profileImage: File? = null
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    val selct: Long = 0
    private var startTime: Long = 0
    private var first: Boolean = false
    private val REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )
        binding.btn.setOnClickListener {
            if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {

                SharedPreferenceUtility.getInstance(requireActivity())
                    .putString("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
                SharedPreferenceUtility.getInstance(requireActivity())
                    .putString("child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString())

                startProjection()
                /* val i = Intent(requireActivity(), ScreenCaptureActivity::class.java)
                 i.putExtra("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
                 i.putExtra("child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString())
                 startActivity(i)*/
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.screenshot_requested),
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper())
                    .postDelayed(
                        {
                            get_child_screenshotClicked()
                        }, 3000
                    )
            }
        }

        binding.btnAddTime.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.attributes?.windowAnimations =
                android.R.style.Widget_Material_ListPopupWindow
            dialog.setContentView(R.layout.add_timer_home)
            val lp = WindowManager.LayoutParams()
            val window: Window = dialog.window!!
            lp.copyFrom(window.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = lp
            val yes_btn: TextView = dialog.findViewById(R.id.yes_btn)
            val btn_add_time: Button = dialog.findViewById(R.id.btn_add_time)
            val dialog_haur_picker: NumberPicker = dialog.findViewById(R.id.dialog_haur_picker)
            val dialog_minuts_picker: NumberPicker = dialog.findViewById(R.id.dialog_minuts_picker)
            dialog_haur_picker.maxValue = 24
            dialog_haur_picker.minValue = 0
            dialog_haur_picker.wrapSelectorWheel = true
            dialog_minuts_picker.maxValue = 59
            dialog_minuts_picker.minValue = 0
            dialog_minuts_picker.wrapSelectorWheel = true
            yes_btn.setOnClickListener { v1: View? ->
                dialog.dismiss()
            }
            btn_add_time.setOnClickListener { v1: View? ->
                try {
                    val selectedhaur: Long = dialog_haur_picker.value.toString().toLong()
                    val selectedminurts: Long = dialog_haur_picker.value.toString().toLong()
                    Timber.tag(TAG).e("onCreateView: selected%s", selectedhaur * 60 * 60 * 1000)
                    Timber.tag(TAG).e("onCreateView: dt%s", selectedminurts * 60 * 1000)
                    val selected: Long = selectedhaur * 60 * 60 * 1000 + selectedminurts * 60 * 1000
                    //  startTime = (selected).toLong()
                    addChildTimer(getDate(startTime), "${selectedhaur}hh:${selectedminurts}ss")
                    //   first = true
                    Timber.tag(TAG).e("onCreateView: startTime%s", getDate(startTime))
                    Timber.tag(TAG)
                        .e("onCreateView: startTime%s", "selectedhaur}hh:${selectedminurts}ss")
                    dialog.dismiss()
                } catch (e: Exception) {
                    dialog.dismiss()
                }
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()

        }
        //checkOverlayPermission()
        //  getScreenShots();
        getChildRemainingTime()
        //  getChildTime()
        return binding.root
    }

    fun getDate(milliSeconds: Long): String? {
        var data: String? = null
        try {

            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis() + milliSeconds
            data = formatter.format(calendar.time)
        } catch (e: Exception) {

        }
        return data
    }

    private fun advanceTimer() {
        try {
            if (!first) {
                binding.timechils.text = "00:00"
            } else {
                if (startTime > 0) {
                    startTime -= 1000
                    var time: Long = (startTime)
                    binding.timechils.text = "" + differenceResult(time)
                    val progress = (time / 1000).toInt()
                    binding.progressBar.progress = binding.progressBar.max - progress
                } else {
                    first = false
                    binding.timechils.text = "00:00"
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getCurrentTimezoneOffset(): String? {
        val tz = TimeZone.getDefault()
        val cal = GregorianCalendar.getInstance(tz)
        val offsetInMillis = tz.getOffset(cal.timeInMillis)
        var offset = String.format(
            "%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs(
                offsetInMillis / 60000 % 60
            )
        )
        offset = "GMT" + (if (offsetInMillis >= 0) "+" else "-") + offset
        return offset
    }

    fun differenceResult(time: Long): String {
        var x: Long = time / 1000

        var seconds = x % 60
        x /= 60
        var minutes = x % 60
        x /= 60
        var hours = (x % 24).toInt()
        x /= 24
        var days = x
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private fun get_child_screenshotClicked() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.get_child_screenshot(map).enqueue(object : Callback<SuccessScreenshotRes?> {
            override fun onResponse(
                call: Call<SuccessScreenshotRes?>,
                response: Response<SuccessScreenshotRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        screenshotRes?.clear()
                        screenshotRes = response.body()!!.result
                        val adapterRideOption =
                            AdapterScreenshotList(
                                requireActivity(),
                                screenshotRes, this@HomeFragment
                            )
                        val numberOfColumns = 3
                        binding.childList.layoutManager = GridLayoutManager(
                            requireActivity(),
                            numberOfColumns
                        )


                        binding.childList.adapter = adapterRideOption
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessScreenshotRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    private fun getChildRemainingTime() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.get_child_remaining_time(map)
            .enqueue(object : Callback<SuccessChildRemainTime?> {
                override fun onResponse(
                    call: Call<SuccessChildRemainTime?>,
                    response: Response<SuccessChildRemainTime?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val datas: SuccessChildRemainTime.ChildRemainTime =
                                response.body()!!.result
                            Timber.tag(" Timer  finalTime--").e("%s", datas.difference_new)
                            try {
                                if (datas.difference_new > 0) {
                                    startTime = 0
                                    var timer = Timer()
                                    startTime = datas.difference_new * 1000
                                    first = true
                                    timer.schedule(timerTask { advanceTimer() }, 1000, 1000)
                                    Log.e(TAG, "startTimestartTimestartTime: " + startTime)
                                } else {

                                }
                                //   MyCountDownTimer(startTime,1000)

                            } catch (e: Exception) {
                                Timber.tag(" Timer  finalTime--").e(e)
                                Timber.tag(" Timer  finalTime--").e(e.localizedMessage)
                                Timber.tag(" Timer  finalTime--").e(e.cause)

                            }

                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccessChildRemainTime?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }

    private fun addChildTimer(timer: String?, sec: String?) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        /*parent_id=1&child_id=1&timer=*/
        val tz = TimeZone.getDefault()
        val id = tz.id
        Timber.tag(TAG).e("onCreateView: getCurrentTimezoneOffset%s", id)
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["timer"] = sec.toString()
        map["final_time"] = timer.toString()
        map["time_zone"] = id.toString()
        Timber.tag(TAG).e("Login user final_timefinal_timefinal_time = %s", map)
        apiInterface.add_child_timer(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    getChildRemainingTime()

                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: SuccessScreenshotRes.ScreenshotList) {
    }

    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {
        var isRunning = false
        override fun onTick(millisUntilFinished: Long) {
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            binding.timechils.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            binding.timechils.text = "0:00"

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                requireActivity().startService(
                    ScreenCaptureService.getStartIntent(
                        requireActivity(), resultCode, data
                    )
                )
            }
        }
    }

    private fun startProjection() {
        val mProjectionManager =
            requireActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mProjectionManager.createScreenCaptureIntent(),
            REQUEST_CODE
        )
    }

    private fun stopProjection() {
        requireActivity().startService(ScreenCaptureService.getStopIntent(requireActivity()))
    }
}
