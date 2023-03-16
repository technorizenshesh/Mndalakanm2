package com.app.mndalakanm.ui.setupKid


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.model.SuccessPairRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.blikoon.qrcodescanner.QrCodeActivity
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentScanQrBinding
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class ScanQrCodeFragment : Fragment() {
    private val REQUEST_CODE_QR_SCAN = 101
    private val MY_PERMISSION_CONSTANT = 5
    private var android_id = " "

    lateinit var binding: FragmentScanQrBinding
    private lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    private var registerId = ""

    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_scan_qr, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        android_id = Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID)
        binding.btnSignIn.setOnClickListener {

            if (checkPermisssionForReadStorage()) {
                val i = Intent(requireContext(), QrCodeActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_QR_SCAN)
            } else {

            }
            /*  val bundle = Bundle()
              bundle.putString("type", "child")
              Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_child_details_fragment,bundle)
       */
        }

        return binding.root
    }

    fun checkPermisssionForReadStorage(): Boolean {
        return if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSION_CONSTANT
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSION_CONSTANT
                )
            }
            false
        } else {

            //  explain("Please Allow Location Permission");
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "COULD NOT GET A GOOD RESULT.")
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image")
            if (result != null) {
                val alertDialog: AlertDialog = AlertDialog.Builder(requireActivity()).create()
                alertDialog.setTitle("Scan Error")
                alertDialog.setMessage("QR Code could not be scanned")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                alertDialog.show()
            }
            return
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            Log.d(TAG, "Have scan result in your app activity :$result")
            if (result != null) {
                if (result.contains(getString(R.string.app_name), true)) {
                    val oldValue = requireActivity().getString(R.string.app_name) + " Code:- ~~"
                    val output = result.replace(oldValue, " ")
                    Log.e(TAG, "onActivityResult: " + output.trim())

                    pairCode(output.trim())
                }
            }
            // s= result.st
            /*  val alertDialog: AlertDialog = AlertDialog.Builder(requireActivity()).create()
              alertDialog.setTitle("Scan result")
              alertDialog.setMessage(result)
              alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                  DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
              alertDialog.show()*/
        }
    }

    private fun pairCode(otp: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["pairing_code"] = otp
        map["mobile_id"] = android_id
        map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        map["lat"] = sharedPref.getStringValue(Constant.LATITUDE).toString()
        map["lon"] = sharedPref.getStringValue(Constant.LONGITUDE).toString()

        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.pairing_code(map).enqueue(object : Callback<SuccessPairRes?> {
            override fun onResponse(
                call: Call<SuccessPairRes?>,
                response: Response<SuccessPairRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        sharedPref.setStringValue(Constant.USER_TYPE, "child")
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        sharedPref.setStringValue(Constant.CHILD_ID, response.body()?.result?.id!!)
                        sharedPref.setStringValue(
                            Constant.CHILD_NAME,
                            response.body()?.result?.name!!
                        )
                        sharedPref.setStringValue(
                            Constant.USER_ID,
                            response.body()?.result?.parentId!!
                        )
                        val bundle = Bundle()
                        bundle.putString("type", "child")
                        bundle.putString("from", "splash")
                        Navigation.findNavController(binding.root).navigate(
                            R.id.action_splash_to_child_details_fragment, bundle
                        )
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessPairRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

}


