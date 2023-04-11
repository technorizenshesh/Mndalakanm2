package com.app.mndalakanm.ui.Home.Apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentUnblockedAppsBinding

class UnblockedAppsFragment : Fragment() {

    lateinit var binding: FragmentUnblockedAppsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_unblocked_apps, container, false)

        return binding.root
    }
}