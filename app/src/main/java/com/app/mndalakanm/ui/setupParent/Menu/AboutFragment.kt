package com.app.mndalakanm.ui.setupParent.Menu

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.mndalakanm.Mndalakanm
import com.techno.mndalakanm.BuildConfig
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.terms.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_terms_fragment)

        }
        binding.privacy.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_privacy_fragment)

        }
        binding.shareApp.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.app.mndalakanm")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.app.mndalakanm")))
            }
        }
        return binding.root
    }
}