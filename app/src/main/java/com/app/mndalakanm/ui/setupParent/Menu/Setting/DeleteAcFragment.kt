package com.app.mndalakanm.ui.setupParent.Menu.Setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentDeleteAcBinding


class DeleteAcFragment : Fragment() {

    lateinit var binding: FragmentDeleteAcBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_delete_ac, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        /*  binding.subscription.setOnClickListener {

          }*/
        return binding.root
    }
}