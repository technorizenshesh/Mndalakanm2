package com.app.mndalakanm.ui.Home.Apps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.techno.mndalakanm.R

import com.techno.mndalakanm.databinding.FragmentAppsBinding

class AppsFragment : Fragment() {
    lateinit var binding: FragmentAppsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_apps,
            container, false
        )
        binding.blockedApps.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_block)
        }
        binding.systemApps.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_system)
        }
        binding.webFiltering.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_web_filtering)
        }

        return binding.root
    }
}