package com.app.mndalakanm.ui.setupParent

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.model.SuccessPairingRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentPairingCodeGenerateBinding
import com.app.mndalakanm.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class PairingCodeGenerateFragment : Fragment() {
    private lateinit var binding: FragmentPairingCodeGenerateBinding
    lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    var type = ""
    private val mServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Extract your data - better to use constants...
            //val IncomingSms = intent.getStringExtra("incomingSms") //
          //  val phoneNumber = intent.getStringExtra("incomingPhoneNumber")
            Log.e("TAG", "onMessageReceived: "+ "21222342141343432")

            Toast.makeText(context,"Child  Paired Successfully",Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_code_to_no_connect, bundle)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            requireActivity().unregisterReceiver(mServiceReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("ChildConnected")
        requireActivity().registerReceiver(mServiceReceiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pairing_code_generate,
            container, false
        )
        if (arguments != null) {
            type = arguments?.getString("type").toString()
        }
        sharedPref = SharedPref(requireContext())
        Timber.tag("TAG").e(
            "FIREBASETOKENFIREBASETOKENFIREBASETOKEN: %s",
            sharedPref.getStringValue(Constant.FIREBASETOKEN)
        )
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
                        binding.codeTxt.text = response.body()!!.result?.pairingCode
                        sharedPref.setStringValue(
                            Constant.PAIRINGCODE,
                            response.body()!!.result?.pairingCode.toString()
                        )
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