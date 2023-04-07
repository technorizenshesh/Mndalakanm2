package  com.app.mndalakanm.service

import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class GPSTracker(private val mContext: Context) : Service(),
    LocationListener {
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false

    // location
    var location: Location? = null
    // latitude

    var latitude = 0.0

    // longitude
    var longitude = 0.0
    lateinit var sharedPref: SharedPref

    @JvmName("getLocation1")
    fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            // getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        return null
                    }
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                            //                            App.editor.putString("LAT", String.valueOf(location.getLatitude()));
//                            App.editor.putString("LON", String.valueOf(location.getLongitude()));
//                            App.editor.apply();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location =
                                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                                Log.e("TAG", "latitudelatitudelatitudelatitude: -=====" + latitude)
                                Log.e(
                                    "TAG",
                                    "longitudelongitudelongitudelongitude: -=====" + longitude
                                )
                                //                                App.editor.putString("LAT", String.valueOf(location.getLatitude()));
//                                App.editor.putString("LON", String.valueOf(location.getLongitude()));
//                                App.editor.apply();
                            }
                        }
                    }
                }
            } else {
                // no network provider is enabled
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                return
            }
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    @JvmName("getLatitude1")
    fun getLatitude(): Double {
        if (location != null) latitude = location!!.latitude
        Log.e("TAG", "getLatitudegetLatitudegetLatitudegetLatitude: " + latitude)
        return latitude
    }

    /**
     * Function to get longitude
     */
    @JvmName("getLongitude1")
    fun getLongitude(): Double {
        if (location != null) longitude = location!!.longitude
        Log.e("TAG", "getLatitudegetLatitudegetLatitudegetLatitude: " + longitude)

        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle("GPS settings")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        alertDialog.setPositiveButton(
            "Settings"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        sharedPref = SharedPref(mContext)
        latitude = location.latitude
        longitude = location.longitude
        sharedPref.setStringValue(Constant.LATITUDE, "" + latitude)
        sharedPref.setStringValue(Constant.LONGITUDE, "" + longitude)
        getLatitude()
        getLongitude()
        Log.e(TAG, "onLocationChanged: " + sharedPref.getStringValue(Constant.USER_TYPE))
        /* if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child",true)){
             sendLetLong(sharedPref,this)
         }*/
        //takeScreenshot()
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }
/*
    @SuppressLint("SuspiciousIndentation")
    private fun sendLetLong(sharedPref: SharedPref, context: Context) {
            val map = HashMap<String, String>()
        */
/*parent_id=1&child_id=1&address=&lat=22.7196&lon*//*

        map["parent_id"]=sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"]=sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["lat"]=sharedPref.getStringValue(Constant.LATITUDE).toString()
        map["lon"]=sharedPref.getStringValue(Constant.LONGITUDE).toString()
        map["date"]=getCurrentDate()
     //   map["address"]=ProjectUtil.getCompleteAddressString(context)
        map["address"] = "demo"
            Timber.tag("ContentValues.TAG").e("final_timefinal_timefinal_timefinal_time"+ map)
        Mndalakanm.loadInterface()?.update_chlid_lat_lon(map)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    Timber.tag("Exception").e("Exception = %s", response.body().toString())

                } catch (e: Exception) {
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                         Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
        }

*/


    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000000 * 1 * 1 // 1 minute
                ).toLong()
    }

    init {
        getLocation()

    }

}