package com.app.mndalakanm.ui.setupParent.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.profile.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_ac_fragment)

        }
        binding.deleteAc.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_delete_ac_fragment)

        }
        binding.changePin.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_change_pin_fragment)

        }
        return binding.root
    }
}