package com.app.mndalakanm.ui.Home.Statistics

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mndalakanm.R
import com.app.mndalakanm.adapter.AdapterScreenshotList
import com.app.mndalakanm.adapter.AdapterTimerList
import com.app.mndalakanm.databinding.FragmentStatisticsBinding
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.model.SuccessTimerListRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.retrofit.SuccessChildHistory
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.TimerListClickListener
import com.bumptech.glide.Glide
import com.db.williamchart.ExperimentalFeature
import com.db.williamchart.slidertooltip.SliderTooltip
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import me.everything.providers.android.browser.BrowserProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class StatisticsFragment : Fragment(), ScreenShotClickListener, TimerListClickListener,
    OnMapReadyCallback {
    lateinit var binding: FragmentStatisticsBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private var screenshotRes: ArrayList<SuccessScreenshotRes.ScreenshotList>? = null
    private var timerList: ArrayList<SuccessTimerListRes.Result>? = null
    lateinit var googleMap: GoogleMap
    //private var chart: BarChart? = null

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    lateinit var today: String

    @OptIn(ExperimentalFeature::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        today = sdf.format(Calendar.getInstance().time)
        //    chart = binding.barChart //this is our barchart

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)



        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)


        try {
            val browserProvider = BrowserProvider(context)
            val bookmarks = browserProvider.bookmarks.list

            Timber.tag("TAG").e("onCreateView: %s", bookmarks.size)
        } catch (e: Exception) {
            Timber.tag("TAG").e("onCreateView: %s", e.message)

        }
        lifecycleScope.launch {
            try {
                getChildTime()
                getScreenShots()
                get_child_plus_time_history()
                // do something with the posts
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TAG", "onCreateView: " + e.message)
                Log.e("TAG", "onCreateView: " + e.localizedMessage)
                Log.e("TAG", "onCreateView: " + e.cause)
            }
        }
        binding.linerChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#81FFFFFF"),
                Color.TRANSPARENT
            )
        binding.linerChart.animation.duration = animationDuration
        binding.linerChart.tooltip =
            SliderTooltip().also {
                it.color = Color.TRANSPARENT
            }

        return binding.root
    }

    /*
    fun setBarChart(data: List<SuccessChildHistory.Result>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((i, da) in data.withIndex()) {
            entries.add(BarEntry(da.timer.toFloat(), i.toFloat()))
            labels.add(da.date)

        }
        val barDataSet = BarDataSet(entries, "Child Time Tracking")
        val data = BarData(barDataSet)
        chart!!.data = data
        chart!!.barData.barWidth = BAR_WIDTH
        barDataSet.color = resources.getColor(R.color.colorPrimary)
        chart!!.setDrawBarShadow(true)
        chart!!.animateY(2000)
    }
*/

    @OptIn(ExperimentalFeature::class)
    private fun get_child_plus_time_history() {
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["date"] = today
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_child_plus_time_history(map)
            .enqueue(object : Callback<SuccessChildHistory?> {
                override fun onResponse(
                    call: Call<SuccessChildHistory?>,
                    response: Response<SuccessChildHistory?>
                ) {
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            Log.e(
                                "TAG",
                                "SuccessChildHistorySuccessChildHistory: " + response.body()
                            )
                            val data = response.body()!!.result
                            val lineSet = LinkedHashMap<String, Float>()

                            Log.e(
                                "TAG",
                                "SuccessChildHistorySuccessChildHistory: " + data.size
                            )
                            for (d in data) {
                                val daa: Float = d.timer.toFloat()
                                lineSet[d.time] = daa
                            }

                            Timber.tag("TAG").e("onResponse: %s", lineSet.toString())
                            binding.linerChart.onDataPointTouchListener = { index, _, _ ->
                                binding.lineChartValue.text =
                                    lineSet.toList()[index].second.toString() + getString(R.string.m)
                            }
                            binding.linerChart.animate(lineSet)
                            //  setBarChart(data)

                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                        Log.e("TAG", "onResponse: " + response.body()!!.lat.toDouble())
                        Log.e("TAG", "onResponse: " + response.body()!!.lon.toDouble())
                        binding.nevLocation.text = response.body()!!.address
                        val sydney = LatLng(
                            response.body()!!.lat.toDouble(),
                            response.body()!!.lon.toDouble()
                        )

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

                    } catch (e: Exception) {
                        //  Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccessChildHistory?>, t: Throwable) {
                    call.cancel()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }


    private fun getScreenShots() {

        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_screenshot(map).enqueue(object : Callback<SuccessScreenshotRes?> {
            override fun onResponse(
                call: Call<SuccessScreenshotRes?>,
                response: Response<SuccessScreenshotRes?>
            ) {
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
                    //  Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessScreenshotRes?>, t: Throwable) {
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
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

    private fun getChildTime() {
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["date"] = today
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_child_timer(map).enqueue(object : Callback<SuccessTimerListRes?> {
            //  apiInterface.get_child_active_reward(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<SuccessTimerListRes?>,
                response: Response<SuccessTimerListRes?>
            ) {
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {

                        // Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                        timerList?.clear()
                        timerList = response.body()!!.result
                        val adapterRideOption =
                            AdapterTimerList(
                                requireActivity(),
                                timerList, this@StatisticsFragment
                            )
                        val numberOfColumns = 1
                        binding.timerList.layoutManager =
                            GridLayoutManager(requireActivity(), numberOfColumns)
                        binding.timerList.adapter = adapterRideOption
                    } else {
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessTimerListRes?>, t: Throwable) {
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })

    }

    override fun onClick(position: Int, model: SuccessTimerListRes.Result) {

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

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
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

    companion object {

        private const val animationDuration = 1000L
    }
}