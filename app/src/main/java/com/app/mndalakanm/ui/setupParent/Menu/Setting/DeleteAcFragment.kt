package com.app.mndalakanm.ui.setupParent.Menu.Setting

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentDeleteAcBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class DeleteAcFragment : Fragment() {
    lateinit var binding: FragmentDeleteAcBinding
    private lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_delete_ac, container, false)

        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnDeleteAc.setOnClickListener {
            DeleteAccountAPI()
        }
        return binding.root
    }

    private fun DeleteAccountAPI() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.delete_parent(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    val status = jsonObject.getString("status")
                    if (status == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        sharedPref.clearAllPreferences()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_menu_to_login_type)


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
            }
        })
    }


}


