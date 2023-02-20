package com.app.mndalakanm.ui.LoginSignup

import android.Manifest.permission
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentLoginTypeBinding
import com.app.mndalakanm.utils.LocationHandler
import com.app.mndalakanm.utils.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.vilborgtower.user.utils.Constant
import timber.log.Timber


class LoginTypeFragment : Fragment() {
    private lateinit var binding: FragmentLoginTypeBinding
    lateinit var navController: NavController
    private val ENABLE_GPS = 3030
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var sharedPref: SharedPref

    var mLocation: Location? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_type,
            container, false
        )
        if (container != null) {
            navController = container.findNavController()
        }
        sharedPref = SharedPref(requireContext())
        binding.childCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "child")
            navController.navigate(R.id.action_splash_to_login_by_child, bundle)

        }
        binding.parentCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_login_by, bundle)

        }
        FirebaseApp.initializeApp(requireContext())
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
            Log.d("TAG", token)
            sharedPref.setStringValue(Constant.FIREBASETOKEN, token + "")
            //  Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
        if (!checkPermission()) {
            requestPermission()
        } else {
            enableGPSAutomatically()
        }
        return binding.root
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.size > 0) {
                val coareAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val fineAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (coareAccepted && fineAccepted) enableGPSAutomatically() else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel(
                                "You need to allow access to both the permissions"
                            ) { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                        arrayOf(
                                            permission.ACCESS_FINE_LOCATION,
                                            permission.ACCESS_COARSE_LOCATION
                                        ),
                                        REQUEST_LOCATION_PERMISSION
                                    )
                                }
                            }
                            return
                        }
                    }
                }
            }
        }
    }

    private fun enableGPSAutomatically() {
        val l_builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationHandler.createLocationRequest()!!)
        l_builder.setAlwaysShow(true) // this is the key ingredient
        val l_settingsClient = LocationServices.getSettingsClient(requireActivity())
        val l_gpsSettingTask = l_settingsClient.checkLocationSettings(l_builder.build())
        l_gpsSettingTask.addOnSuccessListener(requireActivity(),
            OnSuccessListener { p_locationSettingsResponse: LocationSettingsResponse? -> getLocationData() })
        l_gpsSettingTask.addOnFailureListener(requireActivity(),
            OnFailureListener { p_exception: Exception? ->
                if (p_exception is ResolvableApiException) {
                    try {
                        p_exception.startResolutionForResult(
                            requireActivity(),
                            ENABLE_GPS
                        )
                    } catch (e: Exception) {
                    }
                }
            })
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_loc), okListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireActivity(),
            permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            requireActivity(),
            permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationData() {
        val FASTEST_INTERVAL: Long = 50000
        val UPDATE_INTERVAL = (5 * 1000).toLong()
        LocationHandler(
            requireActivity(), null,
            object : LocationHandler.OnLocationUpdateListener {
                override fun onLocationChange(location: Location?) {
                    try {
                        mLocation = location
                        if (mLocation!!.getLatitude() != 0.0 && mLocation!!.getLongitude() != 0.0) {
                            try {
                                val lat: Double = mLocation!!.getLatitude()
                                val longitude: Double = mLocation!!.getLongitude()
                                sharedPref.setStringValue(Constant.LATITUDE, "" + lat)
                                sharedPref.setStringValue(Constant.LONGITUDE, "" + longitude)

                                Timber.e(
                                    "%s%s",
                                    "%s---lang---",
                                    "onLocationChange:   lat  --" + lat.toString(),
                                    longitude.toString()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                    }
                }

                override fun onError(error: String?) {}
            }, UPDATE_INTERVAL, FASTEST_INTERVAL
        )
    }


}