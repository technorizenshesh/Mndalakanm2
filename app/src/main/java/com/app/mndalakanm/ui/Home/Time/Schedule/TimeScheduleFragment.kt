package com.app.mndalakanm.ui.Home.Time.Schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentTimeScheduleBinding

class TimeScheduleFragment : Fragment() {

    lateinit var binding: FragmentTimeScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_time_schedule,
            container, false
        )
        binding.btn.setOnClickListener {
    Navigation.findNavController(binding.root).navigate(R.id.time_nav_to_add_time_scheduleg)
        }
        return binding.root
    }
}