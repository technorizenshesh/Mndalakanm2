package com.app.mndalakanm.ui.setupKid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentChildPermissionBinding

class ChildPermissionFragment : Fragment() {

lateinit var  binding:FragmentChildPermissionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater
            ,R.layout.fragment_child_permission, container, false)

        binding.header. imgHeader .setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "child")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_request_all_permissions_fragment, bundle)


        }




        return  binding.root

    }


}