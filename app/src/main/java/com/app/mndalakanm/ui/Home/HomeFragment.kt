package com.app.mndalakanm.ui.Home

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
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
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.model.SuccessTimerListRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ProjectUtil
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.mtsahakis.mediaprojectiondemo.ScreenCaptureActivity
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentHomeBinding
import com.vilborgtower.user.utils.Constant
import com.vilborgtower.user.utils.RealPathUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


class HomeFragment : Fragment(), ScreenShotClickListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var mProjectionManager: MediaProjectionManager
    var profileImage: File? = null
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    val selct: Long = 0

    private var startTime: Long = System.currentTimeMillis()
    private var first: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        //myCountDownTimer = MyCountDownTimer(20000, 1000)
        // myCountDownTimer!!.start()
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )
        binding.btn.setOnClickListener {
            /* val bitmap =
                 getScreenShotFromView(requireActivity().getWindow().getDecorView().getRootView())
             if (bitmap != null) {
                 saveMediaToStorage(bitmap)
             }*/
            //startProjection()
            val i = Intent(requireActivity(), ScreenCaptureActivity::class.java)
            i.putExtra("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
            i.putExtra("child_id" , sharedPref.getStringValue(Constant.CHILD_ID).toString())
            startActivity(i)

            //     finish()
        }

        var timer = Timer()
        timer.schedule(timerTask {
            advanceTimer()

        }, 0, 60)
        binding.btnAddTime.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.getAttributes()?.windowAnimations =
                android.R.style.Widget_Material_ListPopupWindow
            dialog.setContentView(R.layout.add_timer_home)
            val lp = WindowManager.LayoutParams()
            val window: Window = dialog.getWindow()!!
            lp.copyFrom(window.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            window.setAttributes(lp)
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
                // binding.agePick.setText(numberPicker.value.toString())

            }
            btn_add_time.setOnClickListener { v1: View? ->
                try {
                    val selectedhaur: Long = dialog_haur_picker.value.toString().toLong()
                    val selectedminurts: Long = dialog_haur_picker.value.toString().toLong()
                    Timber.tag(TAG).e("onCreateView: selected%s", selectedhaur * 60 * 60 * 1000)
                    Timber.tag(TAG).e("onCreateView: dt%s", selectedminurts * 60 * 1000)
                    val selected: Long = selectedhaur * 60 * 60 * 1000 + selectedminurts * 60 * 1000
                    startTime = (selected).toLong()
                    addChildTimer(getDate(startTime), "$selectedhaur : $selectedminurts")
                    first = true
                    Timber.tag(TAG).e("onCreateView: startTime%s", startTime)
                    dialog.dismiss()
                    //*- Log.e(TAG, "onCreateView: dt"+oldMillis )
                } catch (e: Exception) {
                    dialog.dismiss()
                }
                //dialog.dismiss()

            }
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()

        }
        //checkOverlayPermission()
        getScreenShots();
        getChildTime()
        return binding.root
    }

    fun getDate(milliSeconds: Long): String? {
        var data: String? = null
        try {

            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS")
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis() + milliSeconds
            data = formatter.format(calendar.time)
        } catch (e: Exception) {

        }
        return data
    }

    fun getTimeDate(milliSeconds: String): Long? {
        var data: Long = 0
        try {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss.SSS", Locale.ENGLISH)
            val localDate: LocalDateTime = LocalDateTime.parse(milliSeconds, formatter)
            data = System.currentTimeMillis() + localDate.atOffset(ZoneOffset.UTC).toInstant()
                .toEpochMilli()
            Log.d(TAG, "Date in milli :: FOR API >= 26 >>> $data")
        } catch (e: Exception) {

        }
        return data
    }

    private fun getChildTime() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_child_timer(map).enqueue(object : Callback<SuccessTimerListRes?> {
            override fun onResponse(
                call: Call<SuccessTimerListRes?>,
                response: Response<SuccessTimerListRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        Timber.tag(TAG)
                            .e("final_timefinal_timefinal_time: %s", response.body()!!.final_time)
                        Timber.tag(TAG)
                            .e(
                                "final_timefinal_timefinal_time: %s",
                                getTimeDate(response.body()!!.final_time)!!
                            )

                        startTime = getTimeDate(response.body()!!.final_time)!!
                        first = true

                        // screenshotRes?.clear()
                        // screenshotRes = response.body()!!.result
                        /*  val adapterRideOption =
                              AdapterScreenshotList(
                                  requireActivity(),
                                  screenshotRes, this@HomeFragment
                              )
                          val numberOfColumns = 3
                          binding.childList.setLayoutManager(
                              GridLayoutManager(
                                  requireActivity(),
                                  numberOfColumns
                              )
                          )


                          binding.childList.adapter = adapterRideOption*/
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessTimerListRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })

    }

    private fun advanceTimer() {
        try {


            if (!first) {
                binding.timechils.text = "00:00"
            } else {
                val currentTime = System.currentTimeMillis().toLong()
                //Log.e(TAG, "onCreateView: startTime" + startTime)
                // Log.e(TAG, "onCreateView: currentTime" + currentTime)
                if (startTime < currentTime) {
                    var time: Long = (currentTime - startTime)
                    binding.timechils.text = "" + differenceResult(time)
                    val progress = (time / 1000).toInt()
                    binding.progressBar.setProgress(binding.progressBar.getMax() - progress)

                } else {
                    first = false
                    binding.timechils.text = "00:00"

                }
            }
        } catch (e: Exception) {

        }
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

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            //  binding.cloctTime.setText(converter(millisUntilFinished))
        }

        override fun onFinish() {

        }
    }

    fun converter(millis: Long): String =
        String.format(
            " %02d : %02d : %02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    millis
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            )
        )


    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                }
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        binding.ivAddPost.setImageBitmap(bitmap)
        val tempUri: Uri = ProjectUtil.getImageUri(requireContext(), bitmap)!!
        val imag = RealPathUtil.getRealPath(requireContext(), tempUri)
        profileImage = File(imag)
        AddDetails(profileImage!!)
        Toast.makeText(requireContext(), "Captured View and saved to Gallery", Toast.LENGTH_SHORT)
            .show()

    }


    private fun stopProjection() {
        // requireActivity().startService(ScreenCaptureService.getStopIntent(requireContext()))

    }


    private fun AddDetails(mage: File) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val profileFilePart: MultipartBody.Part
        val attachmentEmpty: RequestBody
        if (mage == null) {
            attachmentEmpty = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            profileFilePart = MultipartBody.Part.createFormData(
                "attachment",
                "", attachmentEmpty
            )
        } else {
            profileFilePart = MultipartBody.Part.createFormData(
                "image",
                mage.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), mage!!)
            )
        }
        val namedata = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            sharedPref.getStringValue(Constant.CHILD_ID).toString()
        )
        val register = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            sharedPref.getStringValue(Constant.USER_ID).toString()
        )

        apiInterface.add_screenshot(
            register, namedata, profileFilePart
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        getScreenShots()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Log.e(TAG, "onFailure: " + t.message)
                Log.e(TAG, "onFailure: " + t.cause)
                Log.e(TAG, "onFailure: " + t.localizedMessage)
            }

        })


    }

    private fun getScreenShots() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_screenshot(map).enqueue(object : Callback<SuccessScreenshotRes?> {
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
                        binding.childList.setLayoutManager(
                            GridLayoutManager(
                                requireActivity(),
                                numberOfColumns
                            )
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
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    private fun addChildTimer(timer: String?, sec: String?) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        /*parent_id=1&child_id=1&timer=*/
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["timer"] = sec.toString()
        map["final_time"] = timer.toString()
        Timber.tag(ContentValues.TAG).e("Login user final_timefinal_timefinal_time = %s", map)
        apiInterface.add_child_timer(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {

                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: SuccessScreenshotRes.ScreenshotList) {
    }

}
