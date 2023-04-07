package com.app.mndalakanm.ui.Home

import android.Manifest.permission
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.mndalakanm.MainActivity
import com.app.mndalakanm.adapter.AdapterChildRequestsList
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.model.SuccessChildrReqTime
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.ChildRequestListClickListener
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.LocationHandler
import com.app.mndalakanm.utils.SharedPref
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ActivitySuperviseHomeBinding
import com.techno.mndalakanm.databinding.ChildRequestsBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SuperviseHomeActivity : AppCompatActivity(), ChildRequestListClickListener {
    lateinit var binding: ActivitySuperviseHomeBinding
    lateinit var navController: NavController
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    lateinit var model: ChildData
    lateinit var dialog: Dialog
    private val ENABLE_GPS = 3030
    private val REQUEST_LOCATION_PERMISSION = 2
    var mLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supervise_home)
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home_fragment_container)
                    as NavHostFragment? ?: return
        navController = host.navController
        dialog = Dialog(this@SuperviseHomeActivity)
        setupWithNavController(binding.navView, navController)
        sharedPref = SharedPref(this)
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
//        val database = Firebase.database.reference
//        database.useAppCheck(AppCheckInterceptor())
//        val appCheckTokenListener = object : AppCheckTokenListener {
//            override fun onAppCheckTokenChanged(token: AppCheckToken) {
//                // Implement your logic to refresh the AppCheck token here
//            }
//
//            override fun onAppCheckTokenError(error: Throwable) {
//                // Handle any errors that occur while refreshing the AppCheck token here
//            }
//        }
//        database.useAppCheck(AppCheckInterceptor(appCheckTokenListener))
        apiInterface = ApiClient.getClient(this)!!.create(ProviderInterface::class.java)
        model = sharedPref.getChildDetails(Constant.CHILD_Data)
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            binding.imgHeader.setImageDrawable(this.getDrawable(R.drawable.logout_child))
        }
        binding.imgHeader.setOnClickListener {
            if (sharedPref.getStringValue(Constant.USER_TYPE)
                    .equals("child", true)
            ) {
                sharedPref.clearAllPreferences()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                onBackPressed()
            }

        }
        binding.tvLogo.text = model.name
        binding.imgMenu.setOnClickListener {
            getTimeRequestd()
        }
        if (!checkPermission()) {
            requestPermission()
        } else {
            enableGPSAutomatically()
        }
    }

    private fun getTimeRequestd() {
        DataManager.instance.showProgressMessage(
            this@SuperviseHomeActivity,
            getString(R.string.please_wait)
        )
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["status"] = "Pending"
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_plus_time_request(map).enqueue(
            object : Callback<SuccessChildrReqTime?> {
                override fun onResponse(
                    call: Call<SuccessChildrReqTime?>,
                    response: Response<SuccessChildrReqTime?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val data: ArrayList<SuccessChildrReqTime.Result> =
                                response.body()!!.result
                            OpenTimeRequests(data)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            applicationContext,
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccessChildrReqTime?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                }
            })
    }

    private fun OpenTimeRequests(data: ArrayList<SuccessChildrReqTime.Result>) {
        val dialogbinding: ChildRequestsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this@SuperviseHomeActivity), R.layout.child_requests, null, false
        )
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
        val adapterRideOption = AdapterChildRequestsList(
            this@SuperviseHomeActivity, data,
            this@SuperviseHomeActivity
        )
        dialogbinding.childList.layoutManager = LinearLayoutManager(this@SuperviseHomeActivity)
        dialogbinding.childList.adapter = adapterRideOption
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }


    fun checkAccessibilityPermission(): Boolean {
        var accessEnabled = 0
        try {
            accessEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return if (accessEnabled == 0) {
            // if not construct intent to request permission
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // request permission via start activity for result
            startActivity(intent)
            false
        } else {
            true
        }
    }

    override fun onResume() {
        //  getTimerApi()
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            //  addChildDatatoserver()
        }
        super.onResume()
    }

    override fun onClick(position: Int, model: SuccessChildrReqTime.Result, status: String) {
        AcceptReject(model.id, status)
    }

    private fun AcceptReject(id: String, status: String) {
        DataManager.instance.showProgressMessage(
            this@SuperviseHomeActivity,
            getString(R.string.please_wait)
        )
        val map = HashMap<String, String>()
        map["request_id"] = id
        map["status"] = status
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.accept_reject_time_request(map).enqueue(
            object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        recreate()
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            applicationContext,
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                    dialog.dismiss()

                }
            })
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()) {
                val coareAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val fineAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (coareAccepted && fineAccepted) enableGPSAutomatically() else {
                    if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)) {
                        showMessageOKCancel(
                            "You need to allow access to both the permissions"
                        ) { dialog, which ->
                            requestPermissions(
                                arrayOf(
                                    permission.ACCESS_FINE_LOCATION,
                                    permission.ACCESS_COARSE_LOCATION
                                ),
                                REQUEST_LOCATION_PERMISSION
                            )
                        }
                        return
                    }
                }
            }
        }
    }

    private fun enableGPSAutomatically() {
        val l_builder = LocationHandler.createLocationRequest()?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }
        l_builder?.setAlwaysShow(true) // this is the key ingredient
        val l_settingsClient = LocationServices.getSettingsClient(this)
        val l_gpsSettingTask = l_builder?.build()
            ?.let { l_settingsClient.checkLocationSettings(it) }
        l_gpsSettingTask?.addOnSuccessListener(
            this
        ) { p_locationSettingsResponse: LocationSettingsResponse? -> getLocationData() }
        l_gpsSettingTask?.addOnFailureListener(
            this
        ) { p_exception: java.lang.Exception? ->
            if (p_exception is ResolvableApiException) {
                try {
                    p_exception.startResolutionForResult(
                        this@SuperviseHomeActivity,
                        ENABLE_GPS
                    )
                } catch (e: java.lang.Exception) {
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@SuperviseHomeActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_FINE_LOCATION)
        val result1 =
            ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationData() {
        val FASTEST_INTERVAL: Long = 50000
        val UPDATE_INTERVAL = (5 * 1000).toLong()
        LocationHandler(
            this@SuperviseHomeActivity, null,
            object : LocationHandler.OnLocationUpdateListener {
                override fun onLocationChange(location: Location?) {
                    try {
                        mLocation = location
                        if (mLocation!!.latitude != 0.0 && mLocation!!.longitude != 0.0) {
                            try {
                                val lat = mLocation!!.latitude
                                val longitude = mLocation!!.longitude
                                // session.setHOME_LAT(lat.toString())
                                //  session.setHOME_LONG(longitude.toString())
                                //  session.setRestraID(


                                Log.e(
                                    "TAG", "onLocationChange:   lat  --" + lat.toString() +
                                            "---lang---" + longitude.toString()
                                )
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }

                override fun onError(error: String?) {}
            }, UPDATE_INTERVAL, FASTEST_INTERVAL
        )
    }
}