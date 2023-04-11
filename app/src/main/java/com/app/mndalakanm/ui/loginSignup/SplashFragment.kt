package com.app.mndalakanm.ui.loginSignup

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentSplashBinding
import com.app.mndalakanm.utils.Constant
import com.vilborgtower.user.utils.Utils
import timber.log.Timber

class SplashFragment : Fragment() {

    lateinit var utils: Utils
    lateinit var binding: FragmentSplashBinding
    lateinit var navController: NavController
    lateinit var sharedPref: SharedPref
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_splash, container,
            false
        )
        if (container != null) {
            navController = container.findNavController()
        }
        sharedPref = SharedPref(requireContext())
        //utils = Utils(requireContext())
        // utils?.getFirebaseRegisterId()
        //  checkForPermission()
        handlerMethod()
        // checkForPermission()
        //   askNotificationPermission()

        return binding.root
    }

    fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1000
            )
        } else {
            // already permission granted
            handlerMethod()
            //   askNotificationPermission()
        }
    }

    fun handlerMethod() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPref.getBooleanValue(Constant.IS_LOGIN)) {
                Timber.tag("TAG").e(
                    "FIREBASETOKENFIREBASETOKENFIREBASETOKEN: %s",
                    sharedPref.getStringValue(Constant.FIREBASETOKEN)
                )

                if (sharedPref.getStringValue(Constant.USER_TYPE)
                        .equals("provider", true)
                ) {
                    Timber.tag(TAG).e("handlerMethod: %s", sharedPref.getStringValue(Constant.LOCK))

                    /* if (!sharedPref.getStringValue(Constant.LOCK).equals("1")){

                    }else {*/
                    if (sharedPref.getStringValue(Constant.LOCK).equals("1")) {
                        //  action_splash_to_pin

                        //   navController.navigate(R.id.action_splash_to_provider)
                        navController.navigate(R.id.action_splash_to_pin)

                    } else {
                        navController.navigate(R.id.action_login_no_to_set_pin)

                    }
                    // }

                } else {
                    Timber.tag(TAG)
                        .e("handlerMethod: %s", sharedPref.getStringValue(Constant.CHILD_NAME))


                    if (sharedPref.getStringValue(Constant.LOCK).equals("1")) {
                        navController.navigate(R.id.action_splash_to_pin)

                    } else {
                        navController.navigate(R.id.action_login_no_to_set_pin)

                    }

                    /*     if (sharedPref.getStringValue(Constant.CHILD_NAME).equals("", true)) {

                        val bundle = Bundle()
                        bundle.putString("type", "child")
                        bundle.putString("from", "splash")
                        Navigation.findNavController(binding.root).navigate(
                            R.id.action_splash_to_child_details_fragment, bundle
                        )
                        //  navController.navigate(R.id.action_splash_to_provider)

                    } else {
                        // navController.navigate(R.id.action_splash_to_provider)
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                SuperviseHomeActivity::class.java
                            )
                        )
                        requireActivity().finish()

                    }*/
                }


            } else {
            navController.navigate(R.id.action_splash_to_login_type)
                /*requireActivity().startActivity(
                    Intent(
                        requireContext(),
                        MainActivity::class.java
                    )
                )
                requireActivity().finish()*/
            }
        }, 3000)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) { // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED
            ) {
                handlerMethod()
            } else {
                utils.showToast(getString(R.string.permission_denied))
            }
        }
    }

/*
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
*/
}