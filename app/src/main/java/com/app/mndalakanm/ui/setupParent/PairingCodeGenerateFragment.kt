package com.app.mndalakanm.ui.setupParent
import com.app.mndalakanm.utils.DataManager

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.model.SuccessPairingRes
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentPairingCodeGenerateBinding
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.utils.SharedPref
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PairingCodeGenerateFragment : Fragment() {
    private lateinit var binding: FragmentPairingCodeGenerateBinding
    lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    var type = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pairing_code_generate,
            container, false
        )
        if (getArguments() != null) {
            type = arguments?.getString("type").toString()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.imgHeader.setOnClickListener {
         //   activity?.onBackPressed()
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_code_to_no_connect, bundle)

        }
        binding.otherPairingCodes.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_other_pairing_options, bundle)
        }
        generate_pairing_code()
        return binding.root
    }

    private fun generate_pairing_code() {
     DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        var map = HashMap<String, String>()
        map.put("user_id", sharedPref.getStringValue(Constant.USER_ID).toString())
        Log.e(ContentValues.TAG, "Login user Request = $map")
        apiInterface.generate_pairing_code(map).enqueue(object : Callback<SuccessPairingRes?> {
            override fun onResponse(
                call: Call<SuccessPairingRes?>,
                response: Response<SuccessPairingRes?>
            ) {
             DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        binding.codeTxt.setText(response.body()!!.result?.pairingCode)
                        sharedPref.setStringValue(Constant.PAIRINGCODE,response.body()!!.result?.pairingCode.toString())
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                 DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<SuccessPairingRes?>, t: Throwable) {
             DataManager.instance.hideProgressMessage()
                Log.e(ContentValues.TAG, "onFailure: " + t.localizedMessage)
                Log.e(ContentValues.TAG, "onFailure: " + t.cause.toString())
                Log.e(ContentValues.TAG, "onFailure: " + t.message.toString())
            }

        })


    }
}