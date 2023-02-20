package com.app.mndalakanm.ui.Home.Time

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentTimeBinding


class TimeFragment : Fragment() {

    lateinit var binding: FragmentTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false)
        binding.daily.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.time_nav_to_daily)
        }
        binding.schedule.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.time_nav_to_time_scheduleg)
        }
        binding.timeReward.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.time_nav_to_time_reward)
        }



        return binding.root
    }

}