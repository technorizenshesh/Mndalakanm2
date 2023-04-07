package com.app.mndalakanm.ui.Home.Geo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
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
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.adapter.AdapterDaysList
import com.app.mndalakanm.model.SuccessChildPlacesRes
import com.app.mndalakanm.model.SuccessChildlocation
import com.app.mndalakanm.model.WeekDays
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.service.GPSTracker
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.DayClickListener
import com.app.mndalakanm.utils.SharedPref
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.widget.Autocomplete
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.BottemSheeetDetailsBinding
import com.techno.mndalakanm.databinding.FragmentGeoBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class GeoFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener, DayClickListener {
    lateinit var googleMap: GoogleMap
    lateinit var gpsTracker: GPSTracker
    lateinit var sharedPref: SharedPref
    val AUTOCOMPLETE_REQUEST_CODE = 9
    private lateinit var apiInterface: ProviderInterface
    lateinit var binding: FragmentGeoBinding
    private var placeLatitude = ""
    private var placeLongitude = ""
    private var placeLocation = ""
    private var myLatitude = 00.00
    private var myLongitude = 00.00
    lateinit var bottombinding: BottemSheeetDetailsBinding
    var myMarker2: Marker? = null
    var myMarker: Marker? = null
    var marker = arrayOfNulls<Marker?>(2) //change length of array according to you
    var array: Array<WeekDays>? = null
    var daysAdapter: AdapterDaysList? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    lateinit var today: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_geo,
            container, false
        )
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        gpsTracker = GPSTracker(requireActivity())
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        //Places.initialize(requireActivity(), getString(R.string.api_key))
        try {
            today = sdf.format(Calendar.getInstance().time)
            array = getCurrentWeekDates()
            for (i in array!!) {
                Log.e("TAG", "onCreateView: " + i)
            }
            daysAdapter = AdapterDaysList(requireActivity(), array!!, this@GeoFragment)
            binding.recycleDays.layoutManager = GridLayoutManager(requireContext(), 7)
            binding.recycleDays.adapter = daysAdapter

            binding.btnSignIn.setOnClickListener {
                binding.mapLay.visibility = View.VISIBLE
                binding.requestLay.visibility = View.GONE

            }

            if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
                //  addChildDatatoserver()
                binding.addNewPlace.visibility = View.GONE

            } else {
                getChildRemainingTime(today)
            }
            binding.addNewPlace.setOnClickListener {
                if (placeLongitude == "") {
                    Toast.makeText(
                        requireContext(),
                        "Please pick Place Location on Map First",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    openBottomSheet()

                }
            }
            if (gpsTracker.canGetLocation()) {
                myLatitude = gpsTracker.latitude
                myLongitude = gpsTracker.longitude
                Log.e("TAG", "onCreateView: " + myLatitude)
                Log.e("TAG", "onCreateView: " + myLongitude)
                getAddress(LatLng(gpsTracker.latitude, gpsTracker.longitude))
            } else {
                Log.e("TAG", "onCreateView: No Location")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getChildPlaces()
        return binding.root
    }
    private fun getChildRemainingTime(datee: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["date"] = datee
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
    private fun getChildPlaces() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        // map["date"] = "2023-03-30"
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_place(map)
            .enqueue(object : Callback<SuccessChildPlacesRes?> {
                override fun onResponse(
                    call: Call<SuccessChildPlacesRes?>,
                    response: Response<SuccessChildPlacesRes?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val datas: ArrayList<SuccessChildPlacesRes.Result> =
                                response.body()!!.result
                            try {
                                // googleMap.setOnMarkerClickListener{this@GeoFragment }
                                marker = arrayOfNulls(datas.size)
                                var i: Int = 0
                                for (result in datas) {
                                    //TODO_HERE
                                    if (result.type == "Home") {
                                        marker[i] = createMarker(
                                            i,
                                            result.lat.toDouble(),
                                            result.lon.toDouble(),
                                            "#$i",
                                            "",
                                            R.drawable.home_marker
                                        )
                                    } else if (result.type == "Work") {
                                        marker[i] = createMarker(
                                            i,
                                            result.lat.toDouble(),
                                            result.lon.toDouble(),
                                            "#$i",
                                            "",
                                            R.drawable.office_marker
                                        )
                                    } else {
                                        marker[i] = createMarker(
                                            i,
                                            result.lat.toDouble(),
                                            result.lon.toDouble(),
                                            "#$i",
                                            "",
                                            R.drawable.school_marker
                                        )
                                    }
                                    i++
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

                override fun onFailure(call: Call<SuccessChildPlacesRes?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
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
        //googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.isMyLocationEnabled = true
        val sydney = LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }

        googleMap.setOnMapClickListener { latLng ->
            if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
                //  addChildDatatoserver()
            } else {
                Log.e(ContentValues.TAG, "onMapClick: $latLng")
                myMarker2?.remove()
                myMarker2 =googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(latLng.latitude, latLng.longitude))
                        .anchor(0.5f, 0.5f)
                        .title(latLng.toString())
                        .icon(bitmapDescriptorFromVector(requireContext(),R.drawable.pin ))
                        .snippet("snippet")
                )


                   // createMarker(1, latLng.latitude, latLng.longitude, "", "", R.drawable.pin)
                placeLatitude = "" + latLng.latitude
                placeLongitude = "" + latLng.longitude
            }
        }

    }
    override fun onMarkerClick(p0: Marker): Boolean {
        val position = p0.getTag()

        return false
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openBottomSheet() {
        val mBottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        bottombinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.bottem_sheeet_details,
            null,
            false
        )
        // val sheetView: View = mBottomSheetDialog.layoutInflater.inflate(R.layout.bottem_sheeet_details, null)
        mBottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBottomSheetDialog.setContentView(bottombinding.root)
        var type = "Home"
        bottombinding.txtHome.setOnClickListener {
            type = "Home"
            bottombinding.txtHome.background =
                requireContext().getDrawable(R.drawable.border_btn_primary)
            bottombinding.txtHome.setTextColor(requireContext().getColor(R.color.white))
            bottombinding.txtSchool.background =
                requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtSchool.setTextColor(requireContext().getColor(R.color.texts))
            bottombinding.txtWork.background = requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtWork.setTextColor(requireContext().getColor(R.color.texts))

        }
        bottombinding.txtSchool.setOnClickListener {
            type = "School"
            bottombinding.txtSchool.background =
                requireContext().getDrawable(R.drawable.border_btn_primary)
            bottombinding.txtSchool.setTextColor(requireContext().getColor(R.color.white))
            bottombinding.txtHome.background = requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtHome.setTextColor(requireContext().getColor(R.color.texts))
            bottombinding.txtWork.background = requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtWork.setTextColor(requireContext().getColor(R.color.texts))

        }
        bottombinding.txtWork.setOnClickListener {
            type = "Work"
            bottombinding.txtWork.background =
                requireContext().getDrawable(R.drawable.border_btn_primary)
            bottombinding.txtWork.setTextColor(requireContext().getColor(R.color.white))
            bottombinding.txtHome.background = requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtHome.setTextColor(requireContext().getColor(R.color.texts))
            bottombinding.txtSchool.background =
                requireContext().getDrawable(R.drawable.border_gray)
            bottombinding.txtSchool.setTextColor(requireContext().getColor(R.color.texts))
        }
        bottombinding.edtAddress.setText(
            getAddress(
                LatLng(
                    placeLatitude.toDouble(),
                    placeLongitude.toDouble()
                )
            )
        )
        bottombinding.btnSubmit.setOnClickListener { v: View? -> mBottomSheetDialog.dismiss() }
        bottombinding.btnSave.setOnClickListener { v: View? ->
            val name = bottombinding.editName.text.toString()
            val address = bottombinding.edtAddress.text.toString()
            val enter = bottombinding.onEnter.isChecked
            val exit = bottombinding.onExit.isChecked
            val range = bottombinding.rangeBar.valueTo
            Log.e("TAG", "openBottomSheet:   name    --- " + name)
            Log.e("TAG", "openBottomSheet:   address --- " + address)
            Log.e("TAG", "openBottomSheet:    enter  --- " + enter)
            Log.e("TAG", "openBottomSheet:    exit   --- " + exit)
            Log.e("TAG", "openBottomSheet:    range  --- " + range)
            if (name == "") Mndalakanm.showToast(requireContext(), getString(R.string.empty))
            else
                if (address == "") Mndalakanm.showToast(requireContext(), getString(R.string.empty))
                else
                    AppPlace(type, name, address, enter, exit, range, mBottomSheetDialog)
        }
        mBottomSheetDialog.show()
    }
    private fun AppPlace(type: String, name: String, address: String, enter: Boolean, exit: Boolean, range: Float, mBottomSheetDialog: RoundedBottomSheetDialog
    ) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["type"] = type
        map["name"] = name
        map["address"] = address
        map["lat"] = placeLatitude
        map["lon"] = placeLongitude
        map["range"] = "" + range
        map["enter_notification"] = "" + enter
        map["exit_notification"] = "" + exit
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.add_place(map).enqueue(
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
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        mBottomSheetDialog.dismiss()
                        // requireActivity(). recreate()
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            requireContext(),
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
                    mBottomSheetDialog.dismiss()

                }
            })
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                Log.e(ContentValues.TAG, "Place: " + place.name + ", " + place.id)

                placeLocation = place.address
                val latLng = place.latLng
                val latitude = latLng.latitude
                val longitude = latLng.longitude
                placeLatitude = latitude.toString()
                placeLongitude = longitude.toString()
                val address = place.address
                bottombinding.edtAddress.setText(address.toString())
            }
        }
    }
    private fun getAddress(latlang: LatLng): String {
        var cityNames: String = ""
        var stateName: String = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latlang.latitude, latlang.longitude, 1)
        if (addresses != null && addresses.size > 0) {
            //  val cityNamee = addresses[0].toString()
            val cityName = addresses[0].featureName
            val city = addresses[0].locality
            if (addresses[0].thoroughfare != null) stateName = addresses[0].thoroughfare
            else if (addresses[0].premises != null) stateName = addresses[0].premises
            //   Log.e("TAG", "getAddress: cityNamee "+cityNamee)
            Log.e("TAG", "getAddress: " + cityName)
            Log.e("TAG", "getAddress: " + stateName)
            cityNames = cityName + " , " + stateName + " , " + city

        }
        return cityNames
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
    protected fun createMarker(position: Int, latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int): Marker? {
        //val icon = BitmapDescriptorFactory.fromResource(iconResID)
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9f));
        myMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(bitmapDescriptorFromVector(requireContext(), iconResID))
                .snippet(snippet)
        )
        myMarker?.setTag(position)
        return myMarker
    }
    private fun getCurrentWeekDates(): Array<WeekDays> {
        val calendar = Calendar.getInstance()
        val dates = arrayListOf<WeekDays>()
        val daysOfWeek = arrayListOf<String>(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        )

        // Set the calendar to the beginning of the current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        // Add the dates to the array for each day of the current week
        for (i in 0 until 7) {
            val date = calendar.time
            val dateString = sdf.format(date)
            val dayOfWeek = daysOfWeek[i]
            val curretday = Calendar.getInstance().time
            Log.e("TAG", "getCurrentWeekDates:  curretday --" + sdf.format(curretday))
            Log.e("TAG", "getCurrentWeekDates:  dateString --" + dateString)
            if (sdf.format(curretday) == dateString) {
                dates.add(WeekDays("" + i, dateString, dayOfWeek, true))
            } else {
                dates.add(WeekDays("" + i, dateString, dayOfWeek, false))
            }
            // dates.add("$dateString $dayOfWeek")
            calendar.add(Calendar.DATE, 1)
        }
        return dates.toTypedArray()
    }
    override fun onClick(position: Int, model: WeekDays) {
        today = model.date.toString()
        getChildRemainingTime(model.date.toString()) }
}