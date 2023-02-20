package com.app.mndalakanm.ui.Home.Geo

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentGeoBinding
import de.hdodenhof.circleimageview.CircleImageView

class GeoFragment : Fragment(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap

    lateinit var binding: FragmentGeoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_geo,
            container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        binding.btnSignIn.setOnClickListener{
            binding.mapLay.visibility= View.VISIBLE
            binding.requestLay.visibility= View.GONE

        }
         binding.menu1.setOnClickListener{
             openBottemSheet()

         }
        return binding.root
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


        val sydney = LatLng(22.7196, 75.8577)

        val cameraPosition = CameraPosition.Builder()
            .target(sydney) // Sets the center of the map to location user
            .zoom(14f) // Sets the zoom// Sets the tilt of the camera to 30 degrees
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
        googleMap.isMyLocationEnabled = true
    }
    private fun openBottemSheet() {
        val mBottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        val sheetView: View = mBottomSheetDialog.getLayoutInflater().inflate(R.layout.bottem_sheeet_details, null)
        mBottomSheetDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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