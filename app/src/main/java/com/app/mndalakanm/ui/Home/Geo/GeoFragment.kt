package com.app.mndalakanm.ui.Home.Geo

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.model.SuccessChildlocation
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentGeoBinding
import com.vilborgtower.user.utils.Constant
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class GeoFragment : Fragment(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    lateinit var binding: FragmentGeoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_geo,
            container, false
        )
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.btnSignIn.setOnClickListener {
            binding.mapLay.visibility = View.VISIBLE
            binding.requestLay.visibility = View.GONE

        }
        binding.menu1.setOnClickListener {
            openBottemSheet()
        }
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
        getChildRemainingTime()
        return binding.root
    }

    private fun getChildRemainingTime() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["date"] = "2023-02-21"
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_child_location(map)
            .enqueue(object : Callback<SuccessChildlocation?> {
                override fun onResponse(
                    call: Call<SuccessChildlocation?>,
                    response: Response<SuccessChildlocation?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val datas: ArrayList<SuccessChildlocation.Result> =
                                response.body()!!.result
                            try {
                                if (datas.size >= 1) {
                                    for (item in datas) {
                                        val sydney =
                                            LatLng(item.lat.toDouble(), item.lon.toDouble())
                                        val cameraPosition = CameraPosition.Builder()
                                            .target(sydney) // Sets the center of the map to location user
                                            .zoom(14f) // Sets the zoom// Sets the tilt of the camera to 30 degrees
                                            .build()
                                        googleMap.animateCamera(
                                            CameraUpdateFactory.newCameraPosition(
                                                cameraPosition
                                            )
                                        )
                                        googleMap.addMarker(
                                            MarkerOptions()
                                                .position(sydney)
                                                .title("")
                                                .icon(
                                                    bitmapDescriptorFromVector(
                                                        requireContext(),
                                                        R.drawable.pin
                                                    )
                                                )
                                        )
                                        Log.e("TAG", "sydneysydneysydney: " + sydney.toString())
                                    }

                                }


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

                override fun onFailure(call: Call<SuccessChildlocation?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(), R.raw.map_style
                )
            )
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }



        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true
    }

    private fun openBottemSheet() {
        val mBottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        val sheetView: View =
            mBottomSheetDialog.layoutInflater.inflate(R.layout.bottem_sheeet_details, null)
        mBottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBottomSheetDialog.setContentView(sheetView)
        val btn_submit: CircleImageView? = mBottomSheetDialog.findViewById(R.id.btn_submit)
        val btn: Button? = mBottomSheetDialog.findViewById(R.id.btn)
        btn_submit?.setOnClickListener { v: View? ->
            mBottomSheetDialog.dismiss()

        }
        btn?.setOnClickListener { v: View? ->
            mBottomSheetDialog.dismiss()

        }
        mBottomSheetDialog.show()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}