package com.app.mndalakanm.ui.setupParent.Menu.Setting

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.model.SuccessUserProfile
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentAcInfoBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File


class AcInfoFragment : Fragment() {
    lateinit var binding: FragmentAcInfoBinding
    private lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    var profileImage: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ac_info, container, false)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.btnSignIn.setOnClickListener {
            val name: String? = binding.name.text.toString()
            if (name.equals("", true)) {
                binding.name.error = getString(R.string.empty)
            } else
                AddDetails(name!!)
        }
        getProfile()
        return binding.root
    }


    private fun getProfile() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
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
                        binding.name.setText(UserProfile?.firstName)
                        binding.email.setText(UserProfile?.mobile)
                        binding.appsIds.setText("#Mnd" + UserProfile?.id)
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

    private fun AddDetails(name: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))

        val profileFilePart: MultipartBody.Part
        val attachmentEmpty: RequestBody
        if (profileImage == null) {
            attachmentEmpty = "".toRequestBody("text/plain".toMediaTypeOrNull())
            profileFilePart = MultipartBody.Part.createFormData(
                "attachment",
                "", attachmentEmpty
            )
        } else {
            profileFilePart = MultipartBody.Part.createFormData(
                "image",
                profileImage!!.name, profileImage!!
                    .asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        val namedata = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val register = sharedPref.getStringValue(Constant.USER_ID).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        apiInterface.update_user_profile(
            register, namedata, profileFilePart
        ).enqueue(object : Callback<SuccessUserProfile?> {
            override fun onResponse(
                call: Call<SuccessUserProfile?>,
                response: Response<SuccessUserProfile?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body()?.status.equals("1", true)) {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<SuccessUserProfile?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
            }

        })


    }
}