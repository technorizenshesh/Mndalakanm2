package com.app.mndalakanm.ui.setupParent

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.BottemSheeetLanguageBinding
import com.app.mndalakanm
.databinding.FragmentMenuBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


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
        binding.changeLanguage.setOnClickListener {
            openLanguageBottomSheet()
        }
        getProfile()
        return binding.root
    }


    private fun openLanguageBottomSheet() {
        val bottomSheetBinding: BottemSheeetLanguageBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()), R.layout.bottem_sheeet_language,
                null, false
            )
        val bottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
        if (sharedPref.getStringValue(Constant.LANGUAGE) == "ar") {
            bottomSheetBinding.arb.isChecked = true
        } else if (sharedPref.getStringValue(Constant.LANGUAGE) == "en") {
            bottomSheetBinding.eng.isChecked = true
        } else {
            bottomSheetBinding.kur.isChecked = true

        }
        bottomSheetBinding.englishRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "en")
            val language = "English"
            changeLanguageAPI(language)
            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            bottomSheetDialog.dismiss()
            activity?.recreate()
        }
        bottomSheetBinding.arabicRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "ar")
            val language = "Arabic"
            changeLanguageAPI(language)

            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            bottomSheetDialog.dismiss()
            activity?.recreate()

        }
        bottomSheetBinding.kurdicRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "ku")
            val language = "Kurdish"
            changeLanguageAPI(language)

            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            bottomSheetDialog.dismiss()
            activity?.recreate()

        }
    }

    private fun changeLanguageAPI(language: String) {
        val map = HashMap<String, String>()
        map["user_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["language"] = language
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_user_profile(map).enqueue(object : Callback<SuccessUserProfile?> {
            override fun onResponse(
                call: Call<SuccessUserProfile?>,
                response: Response<SuccessUserProfile?>
            ) {

            }

            override fun onFailure(call: Call<SuccessUserProfile?>, t: Throwable) {
                //  DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }


    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, res.displayMetrics)
    }

    private fun getProfile() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["user_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_user_profile(map).enqueue(object : Callback<SuccessUserProfile?> {
            override fun onResponse(
                call: Call<SuccessUserProfile?>,
                response: Response<SuccessUserProfile?>
            ) {
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