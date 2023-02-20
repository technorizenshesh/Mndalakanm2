package com.app.mndalakanm.utils


import android.content.Context
import android.location.Location
import android.os.Looper
import android.os.ResultReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task


class LocationHandler(
    private val context: Context, private val resultReceiver: ResultReceiver?,
    private val onLocationUpdateListener: OnLocationUpdateListener?,
    p_updateInterval: Long, p_fastestInterval: Long
) {
    private val fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val locationCallback: LocationCallback
    private var updateStartedInternally = false

    //-------------------------------------------------------------------------
    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        UPDATE_INTERVAL = p_updateInterval
        FASTEST_INTERVAL = p_fastestInterval
        createLocationRequest()
        deviceLocation
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try {
                    val l_locationList: List<Location> = locationResult.getLocations()
                    if (l_locationList.size > 0) {
                        //The last location in the list is the newest
                        val l_location = l_locationList[l_locationList.size - 1]
                        lastKnownLocation = l_location
                        if (onLocationUpdateListener != null) {
                            onLocationUpdateListener.onLocationChange(l_location)
                            if (updateStartedInternally) {
                                stopLocationUpdate()
                            }
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }// Set the map's camera position to the current location of the device.

    //-------------------------------------------------------------------------
    private val deviceLocation: Unit
        private get() {
            try {
                val l_locationResult: Task<Location?> =
                    fusedLocationProviderClient.getLastLocation()
                l_locationResult.addOnCompleteListener { p_task: Task<Location?> ->
                    if (p_task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = p_task.result
                        if (lastKnownLocation == null) {
                            updateStartedInternally = true
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest!!,
                                locationCallback,
                                Looper.myLooper()
                            )
                        } else {
                            onLocationUpdateListener!!.onLocationChange(lastKnownLocation)
                        }
                    } else {
                        onLocationUpdateListener!!.onError("Can't get Location")
                    }
                }
            } catch (e: SecurityException) {
                onLocationUpdateListener!!.onError(e.message)
            }
        }

    //-------------------------------------------------------------------------
    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    interface OnLocationUpdateListener {
        fun onLocationChange(location: Location?)
        fun onError(error: String?)
    }

    companion object {
        private var locationRequest: LocationRequest? = null
        private var UPDATE_INTERVAL = (5 * 1000).toLong()

        /* 5 secs */
        private var FASTEST_INTERVAL: Long = 2000 /* 2 sec */

        //-------------------------------------------------------------------------
        //other new Methods but not using right now..
        fun createLocationRequest(): LocationRequest? {
            locationRequest = LocationRequest()
            locationRequest!!.setInterval(UPDATE_INTERVAL) //set the interval in which you want to get locations
            locationRequest!!.setFastestInterval(FASTEST_INTERVAL) //if a location is available sooner you can get it (i.e. another app is using the location services)
            locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            return locationRequest
        }
    }
}
