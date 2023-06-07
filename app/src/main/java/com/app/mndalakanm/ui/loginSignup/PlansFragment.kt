package com.app.mndalakanm.ui.loginSignup

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.adapter.AdapterSubscriptionList
import com.app.mndalakanm.model.SuccessSubsRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.SubClickListener
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentPlansBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PlansFragment : Fragment(), SubClickListener {
    private lateinit var binding: FragmentPlansBinding
    var type = ""
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var subslist: ArrayList<SuccessSubsRes.SubsRes>
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

        if (arguments != null) {
            type = arguments?.getString("type").toString()
        }
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.continueCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(container!!).navigate(R.id.action_splash_to_phone, bundle)
        }
        getsubsList()
        return binding.root
    }

    private fun getsubsList() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        //   map["parent_id"] =  sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_subscription_plan(map).enqueue(object : Callback<SuccessSubsRes?> {
            override fun onResponse(
                call: Call<SuccessSubsRes?>,
                response: Response<SuccessSubsRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        subslist = response.body()!!.result
                        val adapterRideOption =
                            AdapterSubscriptionList(requireActivity(), subslist, this@PlansFragment)
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

            override fun onFailure(call: Call<SuccessSubsRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: SuccessSubsRes.SubsRes) {
        //openPicker()
        sharedPref.setStringValue("plan_id", "" + model.id)
        sharedPref.setStringValue("plan_amount", "" + model.totalPrice)
        Timber.tag("TAG").e("onClick: %s", sharedPref.getStringValue("plan_id"))
        Timber.tag("TAG").e("onClick: %s", sharedPref.getStringValue("plan_amount"))

        val bundle = Bundle()
        bundle.putString("type", "parent")
        Navigation.findNavController(binding.root).navigate(R.id.action_plans_to_code, bundle)
        //Toast.makeText(context, "Selected Plan$position", Toast.LENGTH_SHORT).show()
        //addSub(model.id,model.totalPrice,model.forMonth)
    }


    fun openPicker() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.pay_type_picker_dialog)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        val yes_btn: TextView = dialog.findViewById(R.id.pay_now)
        val types: RadioGroup = dialog.findViewById(R.id.types)
        yes_btn.setOnClickListener { v1: View? ->
            types.checkedRadioButtonId
            Log.e("TAG", "openPicker: " + types.checkedRadioButtonId)
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Navigation.findNavController(binding.root).navigate(R.id.action_plans_to_code, bundle)

            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    private fun addSub(id: String?, total_amount: String?, for_month: String?) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["user_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
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
                    val message = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

}