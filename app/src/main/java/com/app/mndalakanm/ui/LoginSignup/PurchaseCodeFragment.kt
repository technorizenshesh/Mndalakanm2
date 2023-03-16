package com.app.mndalakanm.ui.LoginSignup

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentPurchaseCodeBinding
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PurchaseCodeFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseCodeBinding
    var type = ""
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_purchase_code, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        if (arguments != null) {
            type = arguments?.getString("type").toString()

        }
        //Log.e("TAG", "onCreateView: "+ sharedPref.getStringValue("plan_amount"))
        //Log.e("TAG", "onCreateView: "+ sharedPref.getStringValue("plan_id"))
        binding.amount.text = "  $ 300 "
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnRedeem.setOnClickListener {
            if (binding.editNo.text.toString().equals("", true)) {
                binding.editNo.error = "Empty"
            } else {
                purchaseCode(binding.editNo.text.toString())
                /*  val bundle = Bundle()
                  bundle.putString("type", "parent")
                  Navigation.findNavController(container!!)
                      .navigate(R.id.action_splash_to_phone, bundle)*/
            }
        }

        return binding.root
    }

    private fun purchaseCode(code: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["plan_id"] = sharedPref.getStringValue("plan_id").toString()
        map["price"] = "300"
        map["e_mail"] = code
        //parent_id=1&plan_id=1&price=10
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.purchase_plan(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    if (jsonObject.getString("status") == "1") {
                        /* Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        sharedPref.setStringValue(Constant.ISMEMBERSHIP,"true")
                         val bundle = Bundle()
                         bundle.putString("type", "parent")
                         Navigation.findNavController(binding.root).navigate(R.id.action_splash_to_phone,bundle)
                   */
                        SuccessDialog()

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

    fun SuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.success_msg)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            activity?.onBackPressed()
        }, 3000)

    }


}