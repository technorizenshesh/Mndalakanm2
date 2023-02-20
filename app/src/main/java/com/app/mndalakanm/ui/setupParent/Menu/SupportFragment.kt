package com.app.mndalakanm.ui.setupParent.Menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentSupportBinding


class SupportFragment : Fragment() {


    lateinit var binding: FragmentSupportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.supportChat.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_support_chat_fragment)
        }
        return binding.root
    }

}