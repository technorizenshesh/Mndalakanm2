package com.app.mndalakanm.ui.Home

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mndalakanm.adapter.AdapterScreenshotList
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


class HomeFragment : Fragment(), ScreenShotClickListener {
    //var myCountDownTimer: MyCountDownTimer? = null
    lateinit var binding: FragmentHomeBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    var profileImage: File? = null
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    val selct: Long = 0

    private var startTime: Long = 0
    private var first: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
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
            val bitmap =
                getScreenShotFromView(requireActivity().getWindow().getDecorView().getRootView())
            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
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
            /*dialog_haur_picker.setOnValueChangedListener { dialog_haur_picker, i, i1 ->
                Toast.makeText(requireContext(), "" + i, Toast.LENGTH_SHORT).show()
            }*/
            dialog_minuts_picker.maxValue = 59
            dialog_minuts_picker.minValue = 1
            dialog_minuts_picker.wrapSelectorWheel = true
            /* dialog_minuts_picker.setOnValueChangedListener { dialog_haur_picker, i, i1 ->
                 Toast.makeText(requireContext(), "" + i, Toast.LENGTH_SHORT).show()
             }*/
            yes_btn.setOnClickListener { v1: View? ->
                dialog.dismiss()
                // binding.agePick.setText(numberPicker.value.toString())

            }
            btn_add_time.setOnClickListener { v1: View? ->
                try {
                    val selectedhaur: Long = dialog_haur_picker.value.toString().toLong()
                    val selectedminurts: Long = dialog_haur_picker.value.toString().toLong()
                    /*  val formatter = DateTimeFormatter.ofPattern("hh:mm");
                      val dt :LocalDate= LocalDate.parse(selected, formatter);
                      val oldMillis: Long = dt.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                        //  dtNow.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()*/
                    Log.e(TAG, "onCreateView: selected" + selectedhaur * 60 * 60 * 1000)
                    Log.e(TAG, "onCreateView: dt" + selectedminurts * 60 * 1000)
                    val selected: Long = selectedhaur * 60 * 60 * 1000 + selectedminurts * 60 * 1000
                    startTime = (System.currentTimeMillis() + selected).toLong()
                    first =true
                    Log.e(TAG, "onCreateView: startTime" + startTime)
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

    private fun getChildTime() {
    }

    fun advanceTimer() {
        if (!first){
            binding.timechils.text = "00:00"

        }else {
            val currentTime = System.currentTimeMillis().toLong()
            Log.e(TAG, "onCreateView: startTime" + startTime)
            Log.e(TAG, "onCreateView: currentTime" + currentTime)

            var time: Long = (startTime - currentTime)
            binding.timechils.text = "" + differenceResult(time)
            val progress = (time / 1000).toInt()
            binding.progressBar.setProgress(binding.progressBar.getMax() - progress)
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


    /*  override fun onResume() {
          startService()
          super.onResume()
      }
      private fun checkOverlayPermission() {
          if (!Settings.canDrawOverlays(requireActivity())) {
              // send user to the device settings
              val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
              startActivity(myIntent)
          }
      }

      // method for starting the service
      fun startService() {
          if (Settings.canDrawOverlays(requireActivity())) {
              // start the service based on the android version
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  startForegroundService(requireContext(),Intent(requireContext(), ForegroundService::class.java))
              } else
              { requireActivity().startService(Intent(requireContext(), ForegroundService::class.java))
               }
          }else{
              val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
              startActivity(myIntent)
          }
      }*/

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            //  binding.cloctTime.setText(converter(millisUntilFinished))
        }

        override fun onFinish() {
            // binding.progressBar.setProgress(binding.progressBar.getMax() )
            //  binding.cloctTime.setTextColor(requireContext().getColor(R.color.red))
            // binding.cloctTime.setText("00:00")

            //finish()
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

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName =
            sharedPref.getStringValue(Constant.CHILD_ID) + sharedPref.getStringValue(Constant.CHILD_NAME) + Calendar.getInstance().time + ".jpg"
        val database = FirebaseDatabase.getInstance()
        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
        refStorage.putFile(fileUri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                    }
                })

            ?.addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
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

    private fun addChildTimer(timer: String?) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        /*parent_id=1&child_id=1&timer=*/
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["timer"] = timer.toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
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
