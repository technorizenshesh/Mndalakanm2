package com.app.mndalakanm.ui.setupParent.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentSupportChatBinding


class SupportChatFragment : Fragment() {
    lateinit var binding: FragmentSupportChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support_chat, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
    }
}