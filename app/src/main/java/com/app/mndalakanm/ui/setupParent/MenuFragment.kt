package com.app.mndalakanm.ui.setupParent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentMenuBinding
import com.app.mndalakanm.utils.SharedPref


class MenuFragment : Fragment() {


    lateinit var binding: FragmentMenuBinding
    lateinit var sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)
        sharedPref = SharedPref(requireContext())
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.subscription.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_subscr_fragment)

        }
        binding.support.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_support_fragment)

        }
        binding.setting.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_setting_fragment)

        }
        binding.about.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_about_fragment)

        }
        binding.logoutBtn.setOnClickListener {
            sharedPref.clearAllPreferences()
            Navigation.findNavController(binding.root).navigate(R.id.action_menu_to_login_type)


        }
        return binding.root
    }

}