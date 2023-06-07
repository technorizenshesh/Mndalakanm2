package com.app.mndalakanm.ui.setupParent.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentSupportBinding


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
        binding.faq.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_support_faq_fragment)
        }
        binding.deleteAc.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_delete_ac_fragment)
        }
        return binding.root
    }

}