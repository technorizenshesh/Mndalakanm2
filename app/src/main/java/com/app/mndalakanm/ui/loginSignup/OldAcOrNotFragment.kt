package com.app.mndalakanm.ui.loginSignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentOldAcOrNotBinding

class OldAcOrNotFragment : Fragment() {
    lateinit var binding: FragmentOldAcOrNotBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_old_ac_or_not,
                container, false
            )
        binding.oldCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "child")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_login_by_child, bundle)

        }
        binding.newCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "child")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_login_by, bundle)

        }



        return binding.root
    }

}