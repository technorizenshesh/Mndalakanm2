package com.app.mndalakanm.ui.setupParent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentOtherPairingOptionBinding


class OtherPairingOptionFragment : Fragment() {
    private lateinit var binding: FragmentOtherPairingOptionBinding
    lateinit var navController: NavController
    var type = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_other_pairing_option,
            container, false
        )
        if (container != null) {
            navController = container.findNavController()
        }
        if (getArguments() != null) {
            type = arguments?.getString("type").toString()
        }
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.qrCode.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_other_pairing_options_qr, bundle)
        }
        binding.link.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_other_pairing_options_link, bundle)
        }

        return binding.root
    }
}