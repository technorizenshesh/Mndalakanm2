package com.app.mndalakanm.ui.setupParent

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.zxing.WriterException
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentShowQRBinding
import com.app.mndalakanm.utils.SharedPref
import com.vilborgtower.user.utils.Constant


class ShowQRFragment : Fragment() {

    private lateinit var binding: FragmentShowQRBinding
    lateinit var navController: NavController
    var type = ""
    // on below line we are creating
    // a variable for bitmap
    lateinit var bitmap: Bitmap
    lateinit var sharedPref: SharedPref

    // on below line we are creating
    // a variable for qr encoder.
    lateinit var qrEncoder: QRGEncoder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_show_q_r,
            container, false
        )
        if (container != null) {
            navController = container.findNavController()
        }
        if (getArguments() != null) {
            type = arguments?.getString("type").toString()
        }
        sharedPref= SharedPref(requireContext())
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.otherPairingCodes.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_NodeviceParentFragment, bundle)
        }
        binding.scann.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_NodeviceParentFragment, bundle)
        }
// Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        val qrgEncoder = QRGEncoder(requireActivity().getString(R.string.app_name)+" Code:- ~~"+sharedPref.getStringValue(Constant.PAIRINGCODE), null, QRGContents.Type.TEXT, 2048   )
        qrgEncoder.colorBlack = Color.BLACK
        qrgEncoder.colorWhite = Color.WHITE
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap(0)
            // Setting Bitmap to ImageView
            binding.scann.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v(TAG, e.toString())
        }
        return binding.root
    }
}