package com.app.mndalakanm.ui.setupParent.Menu.Setting

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentChangePinBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class ChangePinFragment : Fragment() {

    lateinit var binding: FragmentChangePinBinding
    lateinit var sharedPref: SharedPref
    private val editTexts: Array<EditText>? = null
    private lateinit var apiInterface: ProviderInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_pin, container, false)
        sharedPref = SharedPref(requireActivity())
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        configOtpEditText(
            binding.et1,
            binding.et2,
            binding.et3,
            binding.et4
        )

        binding.btnSignIn.setOnClickListener {
            val et1txt = binding.et1.text.toString()
            val et2txt = binding.et2.text.toString()
            val et3txt = binding.et3.text.toString()
            val et4txt = binding.et4.text.toString()
            if (TextUtils.isEmpty(et1txt)) {
                binding.et1.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et2txt)) {
                binding.et2.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et3txt)) {
                binding.et3.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et4txt)) {
                binding.et4.error = getString(R.string.empty)
            } else {
                val otp = et1txt + et2txt + et3txt + et4txt
                pairCode(otp)
            }
        }

        return binding.root
    }

    private fun pairCode(otp: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        /*https://3tdrive.com/Mndalakanm/webservice/?user_id=1&pin_code=452001*/
        map["pin_code"] = otp
        map["user_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.set_pincode(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    val status = jsonObject.getString("status")
                    if (status.equals("1")) {
                        val result = jsonObject.getJSONObject("result")

                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                Toast.makeText(context, getString(R.string.invalid_code), Toast.LENGTH_SHORT).show()

            }
        })
    }


    fun configOtpEditText(vararg etList: EditText) {
        val afterTextChanged = { index: Int, e: Editable? ->
            val view = etList[index]
            val text = e.toString()

            when (view.id) {
                // first text changed
                etList[0].id -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                }

                // las text changed
                etList[etList.size - 1].id -> {
                    if (text.isEmpty()) etList[index - 1].requestFocus()
                }

                // middle text changes
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