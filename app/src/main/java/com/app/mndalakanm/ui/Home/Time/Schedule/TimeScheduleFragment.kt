package com.app.mndalakanm.ui.Home.Time.Schedule

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.adapter.AdapterChildRewardList
import com.app.mndalakanm.adapter.AdapterScheduleList
import com.app.mndalakanm.model.SuccessScheduleTime
import com.app.mndalakanm.model.SucessRewardList
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ParentRewardListClickListener
import com.app.mndalakanm.utils.ParentScheduleListClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentTimeScheduleBinding
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class TimeScheduleFragment : Fragment() , ParentScheduleListClickListener {
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    lateinit var binding: FragmentTimeScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_time_schedule,
            container, false
        )

        sharedPref = SharedPref(requireActivity())

        apiInterface =
            ApiClient.getClient(requireActivity())!!.create(ProviderInterface::class.java)
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            //  addChildDatatoserver()
            binding.addBtn.visibility = View.GONE

        } else {

        }

        binding.addBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.time_nav_to_add_time_scheduleg)
        }
        getTimeSecAPI()
        return binding.root
    }

    private fun getTimeSecAPI(){
            //
            DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
            val map = HashMap<String, String>()
            map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
            map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
            Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
            apiInterface.get_child_schedule_list(map).enqueue(
                object : Callback<SuccessScheduleTime?> {
                    override fun onResponse(
                        call: Call<SuccessScheduleTime?>,
                        response: Response<SuccessScheduleTime?>
                    ) {
                        DataManager.instance.hideProgressMessage()
                        try {
                            if (response.body() != null && response.body()?.status.equals("1")) {
                                val data: ArrayList<SuccessScheduleTime.Result> = response.body()!!.result
                                //OpenTimeRequests(data)
                                val adapterRideOption = AdapterScheduleList(
                                    requireActivity(),
                                    data,
                                    this@TimeScheduleFragment
                                )
                                binding.childList.adapter = adapterRideOption

                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } catch (e: Exception) {
                            DataManager.instance.hideProgressMessage()
                            Toast.makeText(
                                requireActivity(),
                                "Exception = " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            Timber.tag("Exception").e("Exception = %s", e.message)
                        }
                    }

                    override fun onFailure(call: Call<SuccessScheduleTime?>, t: Throwable) {
                        DataManager.instance.hideProgressMessage()
                        Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                        Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                        Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                    }
                })
        }


    override fun onClick(
        position: Int,
        model: SuccessScheduleTime.Result,
        status: String,
        type: String
    ) {
        Log.e("TAG", "onClick: " + "clicked")
        if (type == "1") {
            addtimerequest(model, status)
        }


    }


    private fun addtimerequest(model: SuccessScheduleTime.Result, status: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["schedul_id"] = model.id
        map["start_time"] = model.start_time
        map["end_time"] = model.end_time
        // map["day"] = dayes
        map["category_id"] = model.category_id
        map["status"] = status
        map["monday"] = model.monday
        map["tuesday"] = model.tuesday
        map["thursday"] = model.thursday
        map["wednesday"] = model.wednesday
        map["friday"] = model.friday
        map["saturday"] = model.saturday
        map["sunday"] = model.sunday
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.update_schedul_time(map).enqueue(
            object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    //DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                       // requireActivity().onBackPressed()
                        getTimeSecAPI()

                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            requireActivity(),
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
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