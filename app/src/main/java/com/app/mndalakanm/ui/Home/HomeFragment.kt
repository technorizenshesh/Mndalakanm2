package com.app.mndalakanm.ui.Home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mndalakanm.adapter.AdapterScreenshotList
import com.app.mndalakanm.model.SuccessChildProfile
import com.app.mndalakanm.model.SuccessChildRemainTime
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.notification.Config
import com.app.mndalakanm.notification.NotifyUserReceiver
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.mtsahakis.mediaprojectiondemo.ScreenCaptureService
import com.mtsahakis.mediaprojectiondemo.SharedPreferenceUtility
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentHomeBinding
import com.techno.mndalakanm.databinding.RequestTimeDialogBinding
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask


class HomeFragment : Fragment(), ScreenShotClickListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var sharedPref: SharedPref
   private lateinit var  myRef :DatabaseReference
    private lateinit var apiInterface: ProviderInterface
    private lateinit var mProjectionManager: MediaProjectionManager
    var profileImage: File? = null
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    val selct: Long = 0
    private var startTime: Long = 0
    private var first: Boolean = false
    private val REQUEST_CODE = 100
    private var Lockdown_mode = ""
    private val mServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Extract your data - better to use constants...
            try {
                val IncomingSms = intent.getStringExtra("pushNotificationModel")
                if (IncomingSms.equals("1", true)) {


                    //  val phoneNumber = intent.getStringExtra("incomingPhoneNumber")
                    Timber.tag("TAG").e("onMessageReceived: " + "21222342141343432")
                    if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {
                        Toast.makeText(context, "Time Received", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.home_nav_to_home_nav)
                    }
                } else {

                    if (IncomingSms.equals("2", true)) {

                        Timber.tag(TAG).e("onReceive: 22222")
                        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {
                            SharedPreferenceUtility.getInstance(requireActivity()).putString(
                                "parent_id", sharedPref.getStringValue(Constant.USER_ID).toString()
                            )
                            SharedPreferenceUtility.getInstance(requireActivity()).putString(
                                "child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString()
                            )
                            SharedPreferenceUtility.getInstance(requireActivity())
                                .putString("request", "1")
                            startProjection(context)
                            /* val i = Intent(requireActivity(), ScreenCaptureActivity::class.java)
                         i.putExtra("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
                         i.putExtra("child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString())
                         startActivity(i)*/
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        sharedPref = SharedPref(requireContext())
        val str= "https://mndalakanm-53f36-default-rtdb.firebaseio.com/LockDown/"+sharedPref.getStringValue(
            Constant.USER_ID).toString()+"/"+sharedPref.getStringValue(Constant.CHILD_ID).toString()+"/Status"
        Log.e(TAG, "onCreateView:----  "+str )
        myRef = FirebaseDatabase.getInstance().reference

        getLockdownOnOff()
        /* myRef.child("LockDown")
             .child(sharedPref.getStringValue(Constant.USER_ID).toString())
             .child(sharedPref.getStringValue(Constant.CHILD_ID).toString())
             .child("Status")*/
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        binding.btn.setOnClickListener {
            if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {

                SharedPreferenceUtility.getInstance(requireActivity())
                    .putString("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
                SharedPreferenceUtility.getInstance(requireActivity())
                    .putString("child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString())

                startProjection(requireContext())
                /* val i = Intent(requireActivity(), ScreenCaptureActivity::class.java)
                 i.putExtra("parent_id", sharedPref.getStringValue(Constant.USER_ID).toString())
                 i.putExtra("child_id", sharedPref.getStringValue(Constant.CHILD_ID).toString())
                 startActivity(i)*/
            } else {
                Toast.makeText(
                    context, getString(R.string.screenshot_requested), Toast.LENGTH_SHORT
                ).show()

                requestScreenshot()


            }
        }
        binding.btnAddTime.setOnClickListener {
            Timber.tag(TAG)
                .e("USER_TYPEUSER_TYPE " + sharedPref.getStringValue(Constant.USER_TYPE).toString())
            if (sharedPref.getStringValue(Constant.USER_TYPE)
                    .toString().equals("provider", true)) {
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
                val dialog_minuts_picker: NumberPicker =
                    dialog.findViewById(R.id.dialog_minuts_picker)
                dialog_haur_picker.maxValue = 4
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
                        val selectedhaur: Int = dialog_haur_picker.value
                        val selectedminurts: Int = dialog_minuts_picker.value
                        Timber.tag(TAG).e("btn_add_time selectedhaur      %s", selectedhaur)
                        Timber.tag(TAG).e(" btn_add_time selectedminurts %s", selectedminurts)
                           var selectedhaur22: Int = 0
                        if (selectedhaur >= 0) {
                            val selectedhaur2: Int = selectedhaur * 60
                            selectedhaur22 = selectedhaur2 + selectedminurts
                        } else {
                            selectedhaur22 = selectedminurts
                        }
                        addChildTimer("" + selectedhaur22, "${selectedhaur}hh:${selectedminurts}ss")
                        dialog.dismiss()
                    } catch (e: Exception) {
                        dialog.dismiss()
                    }
                }
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                dialog.show()
            } else
            {
                val dialogbinding: RequestTimeDialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.request_time_dialog, null, false
                )
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.attributes?.windowAnimations =
                    android.R.style.Widget_Material_ListPopupWindow
                dialog.setContentView(dialogbinding.root)
                val lp = WindowManager.LayoutParams()
                val window: Window = dialog.window!!
                lp.copyFrom(window.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
                window.attributes = lp
                dialogbinding.fiftinMinuts.setOnClickListener {
                    dialogbinding.nolimit.setText("15")
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                }
                dialogbinding.thirtyMinuts.setOnClickListener {
                    dialogbinding.nolimit.setText("30")
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                }
                dialogbinding.fortyfiveMinuts.setOnClickListener {
                    dialogbinding.nolimit.setText("45")
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                }
                dialogbinding.fiftyMinuts.setOnClickListener {
                    dialogbinding.nolimit.setText("50")
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                }

                dialogbinding.nolimit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                        if (s.toString().equals("15")) {
                            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                        } else if (s.toString().equals("30")) {
                            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                        } else if (s.toString().equals("45")) {
                            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                        } else if (s.toString().equals("50")) {
                            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                        } else {
                            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))

                        }

                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })
                dialogbinding.btnAddTime.setOnClickListener { v1: View? ->
                    try {
                        Timber.tag(TAG).e("onCreateView: startTime%s", "selectedhaur")
                        if (dialogbinding.nolimit.text.toString().equals("", true)) {
                            Toast.makeText(
                                requireContext(), getString(R.string.empty), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            addtimerequest(dialogbinding.nolimit.text.toString())
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                    }
                }
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                dialog.show()
            }


        }
        getChildRemainingTime()
        if (sharedPref.getStringValue("Screenshot").equals("true")) {
            startProjection(requireActivity()) }
        binding.refresh.setOnClickListener {
            get_child_screenshotClicked() }
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {
            binding.switchBackLay.isClickable = false
           binding.customSwitch.isClickable = false
            binding.switchBackLay.isFocusable = false
            binding.customSwitch.isFocusable = false
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context?.getPackageName())
                )
                ActivityCompat.startActivityForResult(requireActivity(), intent,1233,null)
            }else{
                if (binding.customSwitch.isChecked){

                    live(requireActivity(),"1")
                }
            }
        }else{

        }


         binding.customSwitch.setOnClickListener {
             Log.e("TAG", "onCreateView: clicked" )
             Log.e("TAG", "onCreateView: clicked"+sharedPref.getStringValue(Constant.USER_TYPE)  )
             if (sharedPref.getStringValue(Constant.USER_TYPE) == "child") {

             } else {
                 if (binding.customSwitch.isChecked) {

                     myRef.child("LockDown")
                         .child(sharedPref.getStringValue(Constant.USER_ID).toString())
                         .child(sharedPref.getStringValue(Constant.CHILD_ID).toString())
                         .child("Status")
                     .setValue("1").addOnCompleteListener {
                             println("completed")
                             Toast.makeText(context, "completed = " , Toast.LENGTH_SHORT).show()

                         }
                         .addOnFailureListener {
                             println("failed")
                             Toast.makeText(context, "failed = " , Toast.LENGTH_SHORT).show()
                         }
                     update_lockdown_modeAPI("1")
                 } else {
                     myRef.child("LockDown")
                         .child(sharedPref.getStringValue(Constant.USER_ID).toString())
                         .child(sharedPref.getStringValue(Constant.CHILD_ID).toString())
                         .child("Status")
                         .setValue("0").addOnCompleteListener {
                             println("completed")
                             Toast.makeText(context, "completed = " , Toast.LENGTH_SHORT).show()

                         }
                         .addOnFailureListener {
                             println("failed")
                             Toast.makeText(context, "failed = " , Toast.LENGTH_SHORT).show()
                         }
                     update_lockdown_modeAPI("0")
                 }
             }
         }
   /* binding.customSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                }*/


        return binding.root
    }

    private fun getLockdownOnOff() {
        // Attach a ValueEventListener to the "users" reference
        val usersListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called whenever the data at the "users" reference changes.
                // Use the dataSnapshot parameter to access the new data.
                Log.e(TAG, "UserUserUserUserUserUserUser: $dataSnapshot")
                Log.e(TAG, "UserUserUserUserUserUserUser: ${dataSnapshot.value}")
                if (dataSnapshot.value.toString() == "1") {
                    binding.customSwitch.isChecked = true
                    if (sharedPref.getStringValue(Constant.USER_TYPE).equals("Child", true)) {
                        live(requireActivity(),dataSnapshot.value.toString())
                    }
                } else {
                    binding.customSwitch.isChecked = false
                    live(requireActivity(),dataSnapshot.value.toString())

                }


            }

            override fun onCancelled(error: DatabaseError) {
                // This method is called if there is an error reading the data.
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }
        myRef.child("LockDown")
            .child(sharedPref.getStringValue(Constant.USER_ID).toString())
            .child(sharedPref.getStringValue(Constant.CHILD_ID).toString())
            .child("Status")
            .addValueEventListener(usersListener)
    }
    private fun live(context: Context,status:String) {
        try {
        val intent = Intent(Config.GET_DATA_LOCKDOWN)
        intent.putExtra("pushNotificationModel", "1")
        intent.putExtra("status", status)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }catch (e:Exception){
         e.printStackTrace()
        }
    }
    private fun requestScreenshot() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(TAG).e("Login user final_timefinal_timefinal_time = %s", map)
        apiInterface.send_child_notification(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    get_child_screenshotStorage()
                    //getChildRemainingTime()
                    /* Handler(Looper.getMainLooper()).postDelayed(
                             {
                                 get_child_screenshotClicked()
                             }, 5000
                         )*/
                } catch (e: Exception) {
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


    private fun advanceTimer() {
        try {
            if (!first) {
                binding.timechils.text = "00:00"
            } else {
                if (startTime > 0) {
                    startTime -= 1000
                    binding.progressBar.max = startTime.toInt()
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


    private fun get_child_screenshotStorage() {
        /*  try{
          val storage = Firebase.storage
          val listRef = storage.reference.child("image/"+sharedPref.getStringValue(Constant.USER_ID).toString()+"/"+sharedPref.getStringValue(Constant.CHILD_ID).toString()+"/"+"Requested/")
              Log.e(TAG, "get_child_screenshotStorage: "+listRef )
              listRef.listAll().addOnCompleteListener { task ->
              if (task.isSuccessful) {
                  Log.e(TAG, "get_child_screenshotStorage: " )
                 // val items = task.result?.items
                      get_child_screenshotClicked()

              } else {
                  // Handle the failure case
              }
          }}catch (e:Exception){
              e.printStackTrace()
          }*/
        try {


            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (binding != null) {
                        get_child_screenshotClicked()
                    }
                }
            }, 8000, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun get_child_screenshotClicked() {
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"]  = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.get_child_screenshot(map).enqueue(object : Callback<SuccessScreenshotRes?> {
            override fun onResponse(
                call: Call<SuccessScreenshotRes?>, response: Response<SuccessScreenshotRes?>
            ) {
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        screenshotRes?.clear()
                        screenshotRes = response.body()!!.result
                        val adapterRideOption = AdapterScreenshotList(
                            requireActivity(), screenshotRes, this@HomeFragment
                        )
                        val numberOfColumns = 3
                        binding.childList.layoutManager = GridLayoutManager(
                            requireActivity(), numberOfColumns
                        )

                        binding.noData.visibility = View.GONE
                        binding.childList.adapter = adapterRideOption
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                        binding.noData.visibility = View.GONE

                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessScreenshotRes?>, t: Throwable) {
                Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(TAG).e("onFailure: %s", t.message.toString())
                binding.noData.visibility = View.VISIBLE

            }
        })
    }

    private fun getChildRemainingTime() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"]  = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.get_child_remaining_time(map)
            .enqueue(object : Callback<SuccessChildRemainTime?> {
                override fun onResponse(
                    call: Call<SuccessChildRemainTime?>, response: Response<SuccessChildRemainTime?>
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
                                    startTime = datas.difference_new * 60 * 1000
                                    Log.e(TAG, "startTimestartTimestartTime: " + startTime)
                                    first = true
                                    timer.schedule(timerTask { advanceTimer() }, 1000, 1000)
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
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        val tz = TimeZone.getDefault()
        val id = tz.id
        Timber.tag(TAG).e("onCreateView: getCurrentTimezoneOffset%s", id)
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        // map["timer"] = getCurrentDateTime().toString()
        map["final_time"] = timer.toString()
        map["time_zone"] = id.toString()
        Timber.tag(TAG).e("Login user final_timefinal_timefinal_time = %s", map)
        apiInterface.add_child_timer(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    //getChildRemainingTime()
                    Navigation.findNavController(binding.root).navigate(R.id.home_nav_to_home_nav)

                } catch (e: Exception) {
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

    private fun addtimerequest(minuts: String?) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        Timber.tag(TAG).e("onCreateView: getCurrentTimezoneOffset%s", id)
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["plus_time"] = minuts.toString()
        Timber.tag(TAG).e("Login userget_plus_time_request  = %s", map)
        apiInterface.plus_time_request(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {

                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
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
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Holo_NoActionBar)
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog.window!!.statusBarColor =
            requireActivity().getResources().getColor(R.color.colorPrimary)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.customview)
        dialog.show()
        val imagewshow: ImageView = dialog.findViewById(R.id.imagewshow)
        val closeimage = dialog.findViewById<View>(R.id.closeimage)
        closeimage.setOnClickListener { dialog.dismiss() }
        Glide.with(requireActivity())
            .load(model.image)
            .into(imagewshow)
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
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
                Log.e(TAG, "onActivityResult: resultCode  " + resultCode)
                Log.e(TAG, "onActivityResult: data  " + data.toString())
                Log.e(TAG, "onActivityResult: data  " + data!!.extras)
                Log.e(
                    TAG,
                    "onActivityResult: sharedPref.getStringValue(\"Screenshot\")  " + sharedPref.getStringValue(
                        "Screenshot"
                    )
                )

                requireActivity().startService(
                    ScreenCaptureService.getStartIntent(
                        requireActivity(),
                        resultCode,
                        data
                    )
                )
                if (sharedPref.getStringValue("Screenshot").equals("true")) {
                    sharedPref.setStringValue("Screenshot", "false")
                    requireActivity().finish()
                }
            }
        }
    }

    fun startProjection(context: Context) {
        val mProjectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE)
    }

    private fun stopProjection() {
        requireActivity().startService(ScreenCaptureService.getStopIntent(requireActivity()))
    }

    override fun onPause() {
        super.onPause()
        try {
            requireActivity().unregisterReceiver(mServiceReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        getChildProfile()
        val filter = IntentFilter()
        filter.addAction("TimeAdded")
        requireActivity().registerReceiver(mServiceReceiver, filter)
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(NotifyUserReceiver(), IntentFilter(Config.GET_DATA_LOCKDOWN))
    }
    private fun update_lockdown_modeAPI(value :String) {
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["lockdown"] = value
        //parent_id=23&child_id=2&lockdown=0
        Timber.tag(ContentValues.TAG).e("lockedlockedlockedlocked = %s", map)
        apiInterface.update_lockdown_mode(map).enqueue(
            object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                }
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }
    private fun getChildProfile() {
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        //parent_id=23&child_id=2&lockdown=0
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_child_profile(map).enqueue(
            object : Callback<SuccessChildProfile?> {
                override fun onResponse(
                    call: Call<SuccessChildProfile?>,
                    response: Response<SuccessChildProfile?>
                ) {
                    Lockdown_mode= response.body()?.result?.lockdown.toString()
                    Toast.makeText(
                        requireActivity(),
                        Lockdown_mode,
                        Toast.LENGTH_SHORT
                    ).show()

                }
                override fun onFailure(call: Call<SuccessChildProfile?>, t: Throwable) {
                    call.cancel()
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }

}
