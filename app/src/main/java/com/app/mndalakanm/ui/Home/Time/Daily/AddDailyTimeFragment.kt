package com.app.mndalakanm.ui.Home.Time.Daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentAddDailyTimeBinding


class AddDailyTimeFragment : Fragment() {

    lateinit var binding: FragmentAddDailyTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_daily_time,
            container, false
        )
        binding.btnEdit.setOnClickListener {
            //addDailyTime()
        }
        binding.editImage.setOnClickListener {
           // addDailyTime()
        }
        return binding.root
    }



}