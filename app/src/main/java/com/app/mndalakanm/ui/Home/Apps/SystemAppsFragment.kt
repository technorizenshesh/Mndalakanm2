package com.app.mndalakanm.ui.Home.Apps
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.R
import com.app.mndalakanm.adapter.AdapterBlockedAppList
import com.app.mndalakanm.databinding.FragmentSystemAppsBinding
import com.app.mndalakanm.model.SuccessChildApps
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

class SystemAppsFragment : Fragment(), OnBlockedAppListItemClickListener {
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
   // private var childApps: ArrayList<SuccessChildApps.Result>? = null

    lateinit var binding: FragmentSystemAppsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_system_apps, container, false)

        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        get_child_plus_time_history()
        return binding.root
    }

    private fun get_child_plus_time_history() {
         DataManager.instance.showProgressMessage(requireActivity(),getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["status"] = ""
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_child_apps(map).enqueue(object :
            Callback<SuccessChildApps?> {
            override fun onResponse(
                call: Call<SuccessChildApps?>,
                response: Response<SuccessChildApps?>
            ) {
                 DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        Log.e("TAG", "SuccessChildHistorySuccessChildHistory: " + response.body())
                      val  childApps = response.body()?.result
                        var adapter = AdapterBlockedAppList(requireActivity(), childApps, this@SystemAppsFragment)
                        binding.appsList.adapter = adapter
                        binding.appsList.setHasFixedSize(true)
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    //  Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessChildApps?>, t: Throwable) {
                call.cancel()
                DataManager.instance.hideProgressMessage()

                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: SuccessChildApps.Result) {
         if (model.status.toString()=="Active"){
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.doyou_want))
            .setPositiveButton(getString(R.string.ok_loc)) { dilogs, _ ->
                dilogs.dismiss()

                addtorestrict(model,"Deactive");
            }
            .setNegativeButton(getString(R.string.cancel)) { dilogs, _ ->
                dilogs.dismiss()

            }
            .create()
            .show()
         }else{
             AlertDialog.Builder(requireContext())
                 .setMessage(getString(R.string.doyou_want))
                 .setPositiveButton(getString(R.string.ok_loc)) { dilogs, _ ->
                     dilogs.dismiss()
                     addtorestrict(model,"Active");
                 }
                 .setNegativeButton(getString(R.string.cancel)) { dilogs, _ ->
                     dilogs.dismiss()

                 }
                 .create()
                 .show()
            }
    }

    private fun addtorestrict(model: SuccessChildApps.Result, s: String)
        {
            DataManager.instance.showProgressMessage(requireActivity(),getString(R.string.please_wait))
            val map = HashMap<String, String>()
         //   map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
            map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
            map["status"] = s
            map["id"] = model.id.toString()
            Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
            apiInterface.remove_child_apps(map).enqueue(object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        get_child_plus_time_history()

                    } catch (e: Exception) {
                        //  Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    call.cancel()
                    DataManager.instance.hideProgressMessage()

                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
        }

}
