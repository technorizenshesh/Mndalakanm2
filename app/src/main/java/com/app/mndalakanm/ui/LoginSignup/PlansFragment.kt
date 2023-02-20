package com.app.mndalakanm.ui.LoginSignup

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.mndalakanm.model.SuccessSubsRes
import com.app.mndalakanm.adapter.AdapterSubscriptionList
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.SubClickListener
import com.app.mndalakanm.retrofit.ProviderInterface
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentPlansBinding
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PlansFragment : Fragment(),SubClickListener {
    private lateinit var binding: FragmentPlansBinding
    var type = ""
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var subslist:  ArrayList<SuccessSubsRes.SubsRes>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_plans,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        if (getArguments() != null) {
            type = arguments?.getString("type").toString()
        }
        binding.header.imgHeader.setOnClickListener{
            activity?.onBackPressed()
        }
        binding.selectedCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(container!!).navigate(R.id.action_splash_to_phone,bundle)
        }
        getsubsList()
        return binding.root
    }
    private fun getsubsList() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
     //   map["parent_id"] =  sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_subscription_plan(map).enqueue(object : Callback<SuccessSubsRes?> {
            override fun onResponse(call: Call<SuccessSubsRes?>, response: Response<SuccessSubsRes?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        subslist = response.body()!!.result
                        val adapterRideOption =
                            AdapterSubscriptionList(requireActivity(),
                                subslist,this@PlansFragment )
                        binding.childList.adapter = adapterRideOption
                    }else{
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessSubsRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: SuccessSubsRes.SubsRes) {
        Toast.makeText(context, "Selected Plan$position", Toast.LENGTH_SHORT).show()
        addSub(model.id,model.totalPrice,model.forMonth)
      }
    private fun addSub(id: String?,total_amount:String?,for_month:String?) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
         map["user_id"] =  sharedPref.getStringValue(Constant.USER_ID).toString()
         map["plan_id"] = id.toString()
         map["total_amount"] = total_amount.toString()
         map["for_month"] = for_month.toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.addsub(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message  = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("type", "parent")
                        Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_phone,bundle)
                    }else{
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