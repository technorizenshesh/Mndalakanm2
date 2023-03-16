package com.app.mndalakanm.ui.LoginSignup

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.adapter.AdapterRedeemCodeList
import com.app.mndalakanm.model.SuccessRedeemCodeRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.RedeemCodeClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentEnterPurchaseCodeBinding
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class EnterPurchaseCodeFragment : Fragment(), RedeemCodeClickListener {
    private lateinit var binding: FragmentEnterPurchaseCodeBinding
    var type = ""
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var subslist: ArrayList<SuccessRedeemCodeRes.Result>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_enter_purchase_code,
            container,
            false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        if (arguments != null) {
            type = arguments?.getString("type").toString()
        }
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnRedeem.setOnClickListener {
            if (binding.editNo.text.toString().equals("", true)) {
                binding.editNo.error = "Empty"
            } else {
                addSub(binding.editNo.text.toString())
                /*  val bundle = Bundle()
                  bundle.putString("type", "parent")
                  Navigation.findNavController(container!!)
                      .navigate(R.id.action_splash_to_phone, bundle)*/
            }
        }
        binding.btnPurchase.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(container!!)
                .navigate(R.id.action_splash_to_purchase_redeem_code, bundle)
        }
        getsubsList()
        return binding.root
    }

    private fun getsubsList() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_redeem_code_plan(map).enqueue(object : Callback<SuccessRedeemCodeRes?> {
            override fun onResponse(
                call: Call<SuccessRedeemCodeRes?>, response: Response<SuccessRedeemCodeRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        subslist = response.body()!!.result
                        val adapterRideOption =
                            AdapterRedeemCodeList(
                                requireActivity(),
                                subslist, this@EnterPurchaseCodeFragment
                            )
                        binding.childList.adapter = adapterRideOption
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessRedeemCodeRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }


    private fun addSub(code: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["redeem_code"] = code
        //parent_id=1&redeem_code=79464464
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.redeem_code(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        sharedPref.setStringValue(Constant.ISMEMBERSHIP, "true")
                        val bundle = Bundle()
                        bundle.putString("type", "parent")
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_splash_to_phone, bundle)
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

    override fun onClick(position: Int, model: SuccessRedeemCodeRes.Result) {
    }

}