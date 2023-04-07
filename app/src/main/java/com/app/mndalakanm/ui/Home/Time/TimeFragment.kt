package com.app.mndalakanm.ui.Home.Time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentTimeBinding
import com.app.mndalakanm.utils.Constant


class TimeFragment : Fragment() {
    lateinit var sharedPref: SharedPref

    lateinit var binding: FragmentTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false)
        sharedPref = SharedPref(requireContext())
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            binding.daily.visibility = View.VISIBLE
        }
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