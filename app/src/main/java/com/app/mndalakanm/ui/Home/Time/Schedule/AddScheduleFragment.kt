package com.app.mndalakanm.ui.Home.Time.Schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentAddScheduleBinding


class AddScheduleFragment : Fragment() {
    lateinit var binding: FragmentAddScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_schedule,
            container, false
        )
        binding.secduType.setOnClickListener{
            binding.secduType.visibility=View.GONE
            binding.secduType.visibility=View.GONE
        }

        return binding.root
    }
}