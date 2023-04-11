package com.app.mndalakanm.ui.Home.Time.Rewards

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.mndalakanm.adapter.AdapterChildRewardList
import com.app.mndalakanm.adapter.AdapterChildRewardRequestsList
import com.app.mndalakanm.adapter.AdapterParentRewardList
import com.app.mndalakanm.model.SuccessChildrRewardReqTime
import com.app.mndalakanm.model.SucessRewardList
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.ChildRequestListClickListener2
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.ParentRewardListClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.AddTimeRewardDialogBinding
import com.app.mndalakanm
.databinding.FragmentTimeRewardBinding
import com.app.mndalakanm
.databinding.ItemChildRewardsRequestBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class TimeRewardFragment : Fragment(), ParentRewardListClickListener,
    ChildRequestListClickListener2 {
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    lateinit var binding: FragmentTimeRewardBinding
    lateinit var dialog_requests: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_reward, container, false)
        sharedPref = SharedPref(requireActivity())

        apiInterface =
            ApiClient.getClient(requireActivity())!!.create(ProviderInterface::class.java)
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            //  addChildDatatoserver()
            binding.btnAdd.visibility = View.GONE
            binding.requestsTxt.visibility = View.GONE

            getTimeRewardsChild()

        } else {
            getTimeRewards()

        }
        binding.btnAdd.setOnClickListener {
            addDailyTime()
        }
        binding.requestsTxt.setOnClickListener {
            see_child_requests_api()
        }
        return binding.root
    }

    private fun see_child_requests_api() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["status"] = "Pending"
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_reward_request(map)
            .enqueue(object : Callback<SuccessChildrRewardReqTime?> {
                override fun onResponse(
                    call: Call<SuccessChildrRewardReqTime?>,
                    response: Response<SuccessChildrRewardReqTime?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val data: ArrayList<SuccessChildrRewardReqTime.Result> =
                                response.body()!!.result
                            OpenTimeRequests(data)
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
                            requireContext(),
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccessChildrRewardReqTime?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                }
            })
    }

    private fun OpenTimeRequests(data: ArrayList<SuccessChildrRewardReqTime.Result>) {
        dialog_requests = Dialog(requireActivity())

        val dialogbinding: ItemChildRewardsRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.item_child_rewards_request,
            null, false
        )
        dialog_requests.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_requests.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_PopupWindow
        dialog_requests.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog_requests.setContentView(dialogbinding.root)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog_requests.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        val adapterRideOption =
            AdapterChildRewardRequestsList(requireActivity(), data, this@TimeRewardFragment)
        dialogbinding.childList.layoutManager = LinearLayoutManager(requireActivity())
        dialogbinding.childList.adapter = adapterRideOption
        dialog_requests.show()

    }


    private fun getTimeRewards() {
        //
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_reward_list(map).enqueue(
            object : Callback<SucessRewardList?> {
                override fun onResponse(
                    call: Call<SucessRewardList?>,
                    response: Response<SucessRewardList?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val data: ArrayList<SucessRewardList.Result> = response.body()!!.result
                            //OpenTimeRequests(data)

                            val adapterRideOption = AdapterParentRewardList(
                                requireActivity(),
                                data,
                                this@TimeRewardFragment
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

                override fun onFailure(call: Call<SucessRewardList?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                }
            })
    }

    private fun getTimeRewardsChild() {
        //
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_child_reward_list(map).enqueue(
            object : Callback<SucessRewardList?> {
                override fun onResponse(
                    call: Call<SucessRewardList?>,
                    response: Response<SucessRewardList?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val data: ArrayList<SucessRewardList.Result> = response.body()!!.result
                            //OpenTimeRequests(data)

                            val adapterRideOption = AdapterChildRewardList(
                                requireActivity(),
                                data,
                                this@TimeRewardFragment
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

                override fun onFailure(call: Call<SucessRewardList?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                }
            })
    }

    private fun addDailyTime() {
        val dialogbinding: AddTimeRewardDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.add_time_reward_dialog, null, false
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(dialogbinding.root)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialogbinding.fiftinMinuts.setOnClickListener {
            dialogbinding.nolimit.setText("15")
            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
        }
        dialogbinding.thirtyMinuts.setOnClickListener {
            dialogbinding.nolimit.setText("30")
            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
        }
        dialogbinding.fortyfiveMinuts.setOnClickListener {
            dialogbinding.nolimit.setText("45")
            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
        }
        dialogbinding.fiftyMinuts.setOnClickListener {
            dialogbinding.nolimit.setText("50")
            dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
            dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
        }
        dialogbinding.nolimit.setText("15")
        dialogbinding.nolimit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                if (s.toString().equals("15")) {
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                } else if (s.toString().equals("30")) {
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                } else if (s.toString().equals("45")) {
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                } else if (s.toString().equals("50")) {
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.colorPrimary))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                } else {
                    dialogbinding.thirtyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftyMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fortyfiveMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))
                    dialogbinding.fiftinMinuts.setTextColor(requireActivity().getColor(R.color.hint_text_color))

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        dialogbinding.btnAddTime.setOnClickListener { v1: View? ->
            try {
                Timber.tag(ContentValues.TAG).e("onCreateView: startTime%s", "selectedhaur")
                if (dialogbinding.nameReward.text.toString().equals("", true)) {
                    Toast.makeText(
                        requireContext(), getString(R.string.empty), Toast.LENGTH_SHORT
                    ).show()
                } else if (dialogbinding.nolimit.text.toString().equals("", true)) {
                    Toast.makeText(
                        requireContext(), getString(R.string.empty), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    addtimerequest(
                        dialogbinding.nameReward.text.toString(),
                        dialogbinding.nolimit.text.toString(),
                        dialog
                    )
                }
            } catch (e: Exception) {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }

    private fun addtimerequest(name: String, time: String, dialog: Dialog) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["time"] = time
        map["name"] = name
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.add_reward(map).enqueue(
            object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
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
                    dialog.dismiss()

                }
            })
    }

    private fun updatetimerequest(model: SucessRewardList.Result, status: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["reward_id"] = model.id
        map["time"] = model.time
        map["name"] = model.name
        map["status"] = status
        //reward_id=1&parent_id=23&child_id=2&name=This%20is%20Tet&time=60&status=Active
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.update_reward_list(map).enqueue(
            object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        //dialog.dismiss()
                        getTimeRewards()


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
                    //  dialog.dismiss()

                }
            })
    }

    override fun onClick(
        position: Int,
        model: SucessRewardList.Result,
        status: String,
        type: String
    ) {
        Log.e("TAG", "onClick: " + "clicked")
        if (type == "1") {
            updatetimerequest(model, status)
        } else if (type == "0") {
            requestForReward(model, status)
        }


    }

    private fun requestForReward(model: SucessRewardList.Result, status: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))

        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["reward_id"] = model.id
        map["plus_time"] = model.time
        val tz = TimeZone.getDefault()
        val id = tz.id
        map["time_zone"] = id.toString()
        //time_zone=&reward_id=1&plus_time=60
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.rewards_request(map).enqueue(
            object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        //dialog.dismiss()
                        getTimeRewardsChild()


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
                    //  dialog.dismiss()

                }
            })
    }

    override fun onClick(position: Int, model: SuccessChildrRewardReqTime.Result, status: String) {

        AcceptReject(model.id, status)
    }

    private fun AcceptReject(id: String, status: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["request_id"] = id
        map["status"] = status
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.accept_reject_reward_request(map).enqueue(
            object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val message = jsonObject.getString("message")
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        dialog_requests.dismiss()
                        // requireActivity(). recreate()
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            requireContext(),
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
                    dialog_requests.dismiss()

                }
            })
    }


}