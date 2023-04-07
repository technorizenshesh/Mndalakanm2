package com.app.mndalakanm.ui.setupKid

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.model.SuccessPairRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentEnterPairingCodeBinding
import com.app.mndalakanm.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class EnterPairingCodeFragment : Fragment() {
    lateinit var binding: FragmentEnterPairingCodeBinding
    private lateinit var apiInterface: ProviderInterface
    private var android_id = " "

    lateinit var sharedPref: SharedPref

    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_enter_pairing_code,
                container,
                false
            )
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        android_id =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        configOtpEditText(
            binding.et1,
            binding.et2,
            binding.et3,
            binding.et4,
            binding.et5,
            binding.et6
        )

        binding.et6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().equals("", true)) {
                    binding.btnSignIn.performClick()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.btnSignIn.setOnClickListener {
            val et1txt = binding.et1.text.toString()
            val et2txt = binding.et2.text.toString()
            val et3txt = binding.et3.text.toString()
            val et4txt = binding.et4.text.toString()
            val et5txt = binding.et5.text.toString()
            val et6txt = binding.et6.text.toString()
            if (TextUtils.isEmpty(et1txt)) {
                binding.et1.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et2txt)) {
                binding.et2.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et3txt)) {
                binding.et3.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et4txt)) {
                binding.et4.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et5txt)) {
                binding.et5.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et6txt)) {
                binding.et6.error = getString(R.string.empty)
            } else {
                val otp = et1txt + et2txt + et3txt + et4txt + et5txt + et6txt
                pairCode(otp)
            }
        }
        binding.btnScanQrIn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "child")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_splash_to_scan_qr_fragment, bundle)
        }
        return binding.root
    }

    private fun pairCode(otp: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["pairing_code"] = otp
        map["mobile_id"] = android_id
        map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        map["lat"] = sharedPref.getStringValue(Constant.LATITUDE).toString()
        map["lon"] = sharedPref.getStringValue(Constant.LONGITUDE).toString()
        val tz = TimeZone.getDefault()
        val id = tz.id
        map["time_zone"] = id.toString()
        Timber.tag(TAG).e("Login user Request = %s", map)
        apiInterface.pairing_code(map).enqueue(object : Callback<SuccessPairRes?> {
            override fun onResponse(
                call: Call<SuccessPairRes?>,
                response: Response<SuccessPairRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        sharedPref.setStringValue(Constant.USER_TYPE, "child")
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        sharedPref.setStringValue(Constant.CHILD_ID, response.body()?.result?.id!!)
                        sharedPref.setStringValue(
                            Constant.CHILD_NAME,
                            response.body()?.result?.name!!
                        )
                        sharedPref.setStringValue(
                            Constant.USER_ID,
                            response.body()?.result?.parentId!!
                        )
                        val bundle = Bundle()
                        bundle.putString("type", "child")
                        bundle.putString("from", "splash")
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_splash_to_child_details_fragment, bundle)
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessPairRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(TAG).e("onFailure: %s", t.message.toString())
                Toast.makeText(context, getString(R.string.invalid_code), Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun configOtpEditText(vararg etList: EditText) {
        val afterTextChanged = { index: Int, e: Editable? ->
            val view = etList[index]
            val text = e.toString()
            when (view.id) {
                etList[0].id -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                }
                etList[etList.size - 1].id -> {
                    if (text.isEmpty()) etList[index - 1].requestFocus()
                }
                else -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                    else etList[index - 1].requestFocus()
                }
            }
            false
        }
        etList.forEachIndexed { index, editText ->
            editText.doAfterTextChanged { afterTextChanged(index, it) }
        }
    }
}
