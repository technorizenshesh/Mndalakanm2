package com.app.mndalakanm.ui.setupParent.Menu

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.adapter.AdapterFAQList
import com.app.mndalakanm.model.successFAQres
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentSupportFaqBinding
import com.app.mndalakanm.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class SupportFAQFragment : Fragment() {
    lateinit var binding: FragmentSupportFaqBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support_faq, container, false)
        sharedPref = SharedPref(requireActivity())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        getChatList()
        return binding.root
    }
    private fun getChatList() {
        val map = HashMap<String, String>()
        map["sender_id"] = "1"
        map["receiver_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_faqs(map).enqueue(object : Callback<successFAQres?> {
            override fun onResponse(
                call: Call<successFAQres?>,
                response: Response<successFAQres?>
            ) {
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        var childsList = response.body()!!.result
                       val adapterRideOption = AdapterFAQList(requireActivity(), childsList)
                        binding.childList.adapter = adapterRideOption
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    //Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<successFAQres?>, t: Throwable) {
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }


}