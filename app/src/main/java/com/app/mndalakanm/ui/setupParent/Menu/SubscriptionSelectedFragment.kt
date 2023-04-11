package com.app.mndalakanm.ui.setupParent.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentSubscriptionSelectedBinding

class SubscriptionSelectedFragment : Fragment() {
    lateinit var binding: FragmentSubscriptionSelectedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_subscription_selected,
                container,
                false
            )
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        /*  binding.subscription.setOnClickListener {

          }*/
        return binding.root
    }
}