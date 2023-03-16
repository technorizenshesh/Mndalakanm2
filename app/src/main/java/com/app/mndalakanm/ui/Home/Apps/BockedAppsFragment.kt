package com.app.mndalakanm.ui.Home.Apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentBockedAppsBinding

class BockedAppsFragment : Fragment() {
    lateinit var binding: FragmentBockedAppsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bocked_apps, container, false)

        return binding.root
    }
}