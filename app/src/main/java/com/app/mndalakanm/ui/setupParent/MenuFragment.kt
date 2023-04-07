package com.app.mndalakanm.ui.setupParent

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.model.SuccessUserProfile
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentMenuBinding
import com.app.mndalakanm.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MenuFragment : Fragment() {

    private lateinit var apiInterface: ProviderInterface

    lateinit var binding: FragmentMenuBinding
    lateinit var sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.subscription.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_subscr_fragment)

        }
        binding.support.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_support_fragment)

        }
        binding.setting.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_setting_fragment)

        }
        binding.about.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_about_fragment)

        }
        binding.logoutBtn.setOnClickListener {
            sharedPref.clearAllPreferences()
            Navigation.findNavController(binding.root).navigate(R.id.action_menu_to_login_type)


        }
        getProfile()
        return binding.root
    }
    private fun getProfile() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["user_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_user_profile(map).enqueue(object : Callback<SuccessUserProfile?> {
            override fun onResponse(call: Call<SuccessUserProfile?>, response: Response<SuccessUserProfile?>)
            {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        val UserProfile = response.body()?.result
                        binding.tvName.setText(UserProfile?.firstName)
                        binding.tvEmail.setText(UserProfile?.mobile)
                       // binding.appsIds.setText("#Mnd" + UserProfile?.id)
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessUserProfile?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

}