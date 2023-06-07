package com.app.mndalakanm.ui.Home.Time.Daily

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.model.SuccessChildDailyLimitsList
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentDailyTimeBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class DailyTimeFragment : Fragment() {

    lateinit var binding: FragmentDailyTimeBinding
    private lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    var store: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_time, container, false)
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        sharedPref = SharedPref(requireContext())
        binding.layMonday.setOnClickListener {
            addDailyTime("monday")
        }
        binding.layTuesday.setOnClickListener {
            addDailyTime("tuesday")
        }
        binding.layWednesday.setOnClickListener {
            addDailyTime("wednesday")
        }
        binding.layThursday.setOnClickListener {
            addDailyTime("thursday")
        }
        binding.layFriday.setOnClickListener {
            addDailyTime("friday")
        }
        binding.laySaturday.setOnClickListener {
            addDailyTime("saturday")
        }
        binding.laySunday.setOnClickListener {
            addDailyTime("sunday")
        }

        binding.mondaySwitch.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("monday")
                else
                    addDailyTimeAPI("monday", "0")
            }
        }
        binding.switchTuesday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("tuesday")
                else
                    addDailyTimeAPI("tuesday", "0")
            }
        }
        binding.switchWednesday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("wednesday")
                else
                    addDailyTimeAPI("wednesday", "0")
            }
        }
        binding.switchThursday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("thursday")
                else
                    addDailyTimeAPI("thursday", "0")
            }
        }
        binding.switchFriday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("friday")
                else
                    addDailyTimeAPI("friday", "0")
            }
        }
        binding.switchSaturday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("saturday")
                else
                    addDailyTimeAPI("saturday", "0")
            }
        }
        binding.switchSunday.setOnCheckedChangeListener { _, ans ->
            if (store == 0) {
            } else {
                if (ans) addDailyTime("sunday")
                else
                    addDailyTimeAPI("sunday", "0")
            }
        }
        getChildRemainingTime()
        return binding.root
    }
    private fun addDailyTime(day: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.add_timer_home)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        val yes_btn: TextView = dialog.findViewById(R.id.yes_btn)
        val btn_add_time: Button = dialog.findViewById(R.id.btn_add_time)
        val dialog_haur_picker: NumberPicker = dialog.findViewById(R.id.dialog_haur_picker)
        val dialog_minuts_picker: NumberPicker =
            dialog.findViewById(R.id.dialog_minuts_picker)
        dialog_haur_picker.maxValue = 4
        dialog_haur_picker.minValue = 0
        dialog_haur_picker.wrapSelectorWheel = true
        dialog_minuts_picker.maxValue = 59
        dialog_minuts_picker.minValue = 0
        dialog_minuts_picker.wrapSelectorWheel = true
        yes_btn.setOnClickListener { v1: View? ->
            dialog.dismiss()
        }
        btn_add_time.setOnClickListener { v1: View? ->
            try {
                val selectedhaur: Int = dialog_haur_picker.value
                val selectedminurts: Int = dialog_minuts_picker.value
                Timber.tag(ContentValues.TAG).e("btn_add_time selectedhaur      %s", selectedhaur)
                Timber.tag(ContentValues.TAG).e(" btn_add_time selectedminurts %s", selectedminurts)
                var selectedhaur22: Int = 0
                if (selectedhaur >= 0) {
                    val selectedhaur2: Int = selectedhaur * 60
                    selectedhaur22 = selectedhaur2 + selectedminurts
                } else {
                    selectedhaur22 = selectedminurts
                }
                Log.e("hereeeeeeee" + selectedhaur22, "${selectedhaur}hh:${selectedminurts}ss")
                addDailyTimeAPI(day, "" + selectedhaur22)
                dialog.dismiss()
            } catch (e: Exception) {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }
    private fun addDailyTimeAPI(day: String, time: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        if (day == "sunday") map["sunday"] = time
        if (day == "monday") map["monday"] = time
        if (day == "tuesday") map["tuesday"] = time
        if (day == "wednesday") map["wednesday"] = time
        if (day == "thursday") map["thursday"] = time
        if (day == "friday") map["friday"] = time
        if (day == "saturday") map["saturday"] = time
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.add_update_weekday_time(map).enqueue(
            object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        Toast.makeText(
                            requireActivity(),
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        getChildRemainingTime()
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
    private fun getChildRemainingTime() {
        store = 0
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = java.util.HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_weekday_time(map)
            .enqueue(object : Callback<SuccessChildDailyLimitsList?> {
                override fun onResponse(
                    call: Call<SuccessChildDailyLimitsList?>,
                    response: Response<SuccessChildDailyLimitsList?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        if (response.body() != null && response.body()?.status.equals("1")) {
                            val datas: SuccessChildDailyLimitsList.Result =
                                response.body()!!.result
                            if (datas.monday == "0") binding.mondaySwitch.isChecked = false
                            else binding.mondaySwitch.isChecked = true
                            binding.timeMonday.text = formTime(datas.monday)

                            if (datas.tuesday == "0") binding.switchTuesday.isChecked = false
                            else binding.switchTuesday.isChecked = true
                            binding.timeTuesday.text = formTime(datas.tuesday)

                            if (datas.wednesday == "0") binding.switchWednesday.isChecked = false
                            else binding.switchWednesday.isChecked = true
                            binding.timeWednesday.text = formTime(datas.wednesday)

                            if (datas.thursday == "0") binding.switchThursday.isChecked = false
                            else binding.switchThursday.isChecked = true
                            binding.timeThursday.text = formTime(datas.thursday)

                            if (datas.friday == "0") binding.switchFriday.isChecked = false
                            else binding.switchFriday.isChecked = true
                            binding.timeFriday.text = formTime(datas.friday)

                            if (datas.saturday == "0") binding.switchSaturday.isChecked = false
                            else binding.switchSaturday.isChecked = true
                            binding.timeSaturday.text = formTime(datas.saturday)

                            if (datas.sunday == "0") binding.switchSunday.isChecked = false
                            else binding.switchSunday.isChecked = true
                            binding.timeSunday.text = formTime(datas.sunday)
                            store = 1
                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccessChildDailyLimitsList?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                }
            })
    }
    private fun formTime(monday: String): String {
        try {
            val mon: Int = monday.toInt()
            return if (mon > 60) {
                ((mon / 60).toString() + "hr")
            } else
                ((mon).toString() + "min")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return monday
    }
}