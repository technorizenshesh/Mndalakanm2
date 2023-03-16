package com.app.mndalakanm.ui.Home.Statistics

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mndalakanm.adapter.AdapterScreenshotList
import com.app.mndalakanm.adapter.AdapterTimerList
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.model.SuccessTimerListRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.TimerListClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.naqdi.chart.model.Line
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentStatisticsBinding
import com.vilborgtower.user.utils.Constant
import me.everything.providers.android.browser.BrowserProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class StatisticsFragment : Fragment(), ScreenShotClickListener, TimerListClickListener,
    OnMapReadyCallback {
    lateinit var binding: FragmentStatisticsBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    private var timerList: ArrayList<SuccessTimerListRes.TimerList>? = null
    lateinit var googleMap: GoogleMap

    val intervalList = listOf("00:00", "06:00", "12:00", "18:00")
    val rangeList = listOf("00", "30m", "60m")
    val lineList = arrayListOf<Line>().apply {
        add(Line("Time", Color.BLUE, listOf(10f, 280f, 88f, 70f)))
        // add(Line("Line 2", Color.RED, listOf(300f, 40f, 38f, 180f, 403f, 201f)))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)



        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.chainChartView.setData(lineList, intervalList, rangeList)
        binding.chainChartView.apply {
            setLineSize(2f)    // size as dp
            setNodeSize(6F)    //size as dp
            setTextSize(10f)
            setTextColor(R.color.colorPrimary)    //color as int
            setFontFamily(Typeface.SANS_SERIF)    //font as typeface
        }
        try {


            val browserProvider = BrowserProvider(context)
            val bookmarks = browserProvider.bookmarks.list

            Timber.tag("TAG").e("onCreateView: %s", bookmarks.size)
        } catch (e: Exception) {
            Timber.tag("TAG").e("onCreateView: %s", e.message)

        }
        getScreenShots()
        getChildTime()
        return binding.root
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
                                screenshotRes, this@StatisticsFragment
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
                    //  Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
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

    override fun onClick(position: Int, model: SuccessScreenshotRes.ScreenshotList) {
        //  TODO("Not yet implemented")
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

                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                        timerList?.clear()
                        timerList = response.body()!!.result
                        val adapterRideOption =
                            AdapterTimerList(
                                requireActivity(),
                                timerList, this@StatisticsFragment
                            )
                        val numberOfColumns = 1
                        binding.timerList.layoutManager = GridLayoutManager(
                            requireActivity(),
                            numberOfColumns
                        )


                        binding.timerList.adapter = adapterRideOption
                    } else {
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

    override fun onClick(position: Int, model: SuccessTimerListRes.TimerList) {

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
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }


        val sydney = LatLng(22.7196, 75.8577)

        val cameraPosition = CameraPosition.Builder()
            .target(sydney) // Sets the center of the map to location user
            .zoom(12f) // Sets the zoom// Sets the tilt of the camera to 30 degrees
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("")
                .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.pin))
        )
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
        //googleMap.isMyLocationEnabled = true
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