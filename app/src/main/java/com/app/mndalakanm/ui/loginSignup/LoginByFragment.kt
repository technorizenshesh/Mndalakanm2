package com.app.mndalakanm.ui.loginSignup

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentLoginByBinding

class LoginByFragment : Fragment() {
    private lateinit var binding: FragmentLoginByBinding
    lateinit var navController: NavController
    var type = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_by,
            container, false
        )
        if (container != null) {
            navController = container.findNavController()
        }
        if (arguments != null) {
            type = arguments?.getString("type").toString()
        }


        binding.prvc.text = Html.fromHtml(getString(R.string.datal))

        binding.btnSignIn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", type)
            navController.navigate(R.id.action_splash_to_login_no, bundle)

        }
        /* binding.parentCard.setOnClickListener {
             val bundle = Bundle()
             bundle.putString("type", "parent")
             navController.navigate(R.id.action_splash_to_login_type)

         }*/

        return binding.root
    }


}