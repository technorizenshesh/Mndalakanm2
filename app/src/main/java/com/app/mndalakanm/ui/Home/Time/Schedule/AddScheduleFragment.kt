package com.app.mndalakanm.ui.Home.Time.Schedule

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.adapter.AdapterSecdCatList
import com.app.mndalakanm.model.SuccesSchedulCategory
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SechduleClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.CatScdDialogBinding
import com.techno.mndalakanm.databinding.FragmentAddScheduleBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class AddScheduleFragment : Fragment(), SechduleClickListener {
    lateinit var binding: FragmentAddScheduleBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    var monday = "Monday"
    var tuesday = "x"
    var wednesday = "x"
    var thursday = "x"
    var friday = "x"
    var saturday = "x"
    var sunday = "x"
    var day = ""
    var scd_name = ""
    var scd_id = ""
    lateinit var dialog: Dialog

    lateinit var sechdule_list: ArrayList<SuccesSchedulCategory.Result>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_schedule,
            container, false
        )
        sharedPref = SharedPref(requireActivity())

        apiInterface =
            ApiClient.getClient(requireActivity())!!.create(ProviderInterface::class.java)
        binding.secduType.setOnClickListener {
            binding.secduType.visibility = View.GONE
            binding.secduType.visibility = View.GONE
        }
        binding.nameReward.setOnClickListener {
            val dialogbinding: CatScdDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.cat_scd_dialog, null, false
            )
            dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.attributes?.windowAnimations =
                android.R.style.Widget_Material_ListPopupWindow
            dialog.setContentView(dialogbinding.root)
            val lp = WindowManager.LayoutParams()
            val window: Window = dialog.window!!
            lp.copyFrom(window.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialogbinding.catList.adapter =
                AdapterSecdCatList(requireContext(), sechdule_list, this@AddScheduleFragment)
            dialog.show()
        }

        binding.startTime.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    if (hourOfDay < 10) {
                        if (minute < 10) {
                            binding.startTime.setText("0" + hourOfDay + ":" + "0" + minute)

                        } else {
                            binding.startTime.setText("0" + hourOfDay + ":" + minute)

                        }

                    } else if (minute < 10) {
                        binding.startTime.setText("" + hourOfDay + ":" + "0" + minute)

                    } else {
                        binding.startTime.setText(String.format("%d:%d", hourOfDay, minute))

                    }

                    //  binding.startTime.setText(String.format("%d : %d", hourOfDay, minute))
                }, hour, minute, true
            )
            mTimePicker.create()
            mTimePicker.show()
        }
        binding.endTime.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    if (hourOfDay < 10) {
                        if (minute < 10) {
                            binding.endTime.setText("0" + hourOfDay + ":" + "0" + minute)

                        } else {
                            binding.endTime.setText("0" + hourOfDay + ":" + minute)

                        }

                    } else if (minute < 10) {
                        binding.endTime.setText("" + hourOfDay + ":" + "0" + minute)

                    } else {
                        binding.endTime.setText(String.format("%d:%d", hourOfDay, minute))

                    }

                    //  binding.startTime.setText(String.format("%d : %d", hourOfDay, minute))
                }, hour, minute, true
            )
            mTimePicker.create()
            mTimePicker.show()
        }
        binding.txtMon.setOnClickListener {
            if (monday == "x") {
                monday = "Monday"
                binding.txtMon.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                monday = "x"
                binding.txtMon.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtTue.setOnClickListener {
            if (tuesday == "x") {
                tuesday = "Tuesday"
                binding.txtTue.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                tuesday = "x"
                binding.txtTue.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtWed.setOnClickListener {
            if (wednesday == "x") {
                wednesday = "Wednesday"
                binding.txtWed.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                wednesday = "x"
                binding.txtWed.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtThu.setOnClickListener {
            if (thursday == "x") {
                thursday = "Thursday"
                binding.txtThu.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                thursday = "x"
                binding.txtThu.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtFri.setOnClickListener {
            if (friday == "x") {
                friday = "Friday"
                binding.txtFri.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                friday = "x"
                binding.txtFri.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtSat.setOnClickListener {
            if (saturday == "x") {
                saturday = "Saturday"
                binding.txtSat.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                saturday = "x"
                binding.txtSat.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }
        binding.txtSun.setOnClickListener {
            if (sunday == "x") {
                sunday = "Sunday"
                binding.txtSun.setTextColor(requireActivity().getColor(R.color.colorPrimary))
            } else {
                sunday = "x"
                binding.txtSun.setTextColor(requireActivity().getColor(R.color.texts))
            }

        }


        binding.btnAdd.setOnClickListener {
            val name = binding.nameReward.text.toString()
            val strt_time = binding.startTime.text.toString()
            val end_time = binding.endTime.text.toString()
            if (monday == "x") {
                day =
                    tuesday + "," + wednesday + "," + thursday + "," + friday + "," + saturday + "," + sunday

            } else {
                day =
                    monday + "," + tuesday + "," + wednesday + "," + thursday + "," + friday + "," + saturday + "," + sunday
            }
            day = day.replace(",x", "")
            Log.e("TAG", "onCreateView: " + day)
            if (name == "") {
                binding.nameReward.error = getString(R.string.empty)
            } else {
                addtimerequest(name, strt_time, end_time, day)
            }
        }
        getCategory()
        return binding.root
    }

    private fun addtimerequest(name: String, strt_time: String, end_time: String, dayes: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        map["start_time"] = strt_time
        map["end_time"] = end_time
        // map["day"] = dayes
        map["category_id"] = scd_id
        map["name"] = name
        map["status"] = "Active"
        if (dayes.contains("Monday")) map["monday"] = "1"
        else map["monday"] = "0"
        if (dayes.contains("Tuesday")) map["tuesday"] = "1"
        else map["tuesday"] = "0"
        if (dayes.contains("Thursday")) map["thursday"] = "1"
        else map["thursday"] = "0"
        if (dayes.contains("Wednesday")) map["wednesday"] = "1"
        else map["wednesday"] = "0"
        if (dayes.contains("Friday")) map["friday"] = "1"
        else map["friday"] = "0"
        if (dayes.contains("Saturday")) map["saturday"] = "1"
        else map["saturday"] = "0"
        if (dayes.contains("Sunday")) map["sunday"] = "1"
        else map["sunday"] = "0"

        //category_id=1&&sunday=1&monday=1&tuesday=1&wednesday=1&thursday=1&friday=1&saturday=1&
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.add_lockdown_time(map).enqueue(object : Callback<ResponseBody?> {
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
                        requireActivity().onBackPressed()
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

    private fun getCategory() {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        map["child_id"] = sharedPref.getStringValue(Constant.CHILD_ID).toString()
        Timber.tag(ContentValues.TAG).e("get_plus_time_request = %s", map)
        apiInterface.get_schedul_category(map).enqueue(
            object :
                Callback<SuccesSchedulCategory?> {
                override fun onResponse(
                    call: Call<SuccesSchedulCategory?>,
                    response: Response<SuccesSchedulCategory?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        sechdule_list = response.body()?.result!!
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<SuccesSchedulCategory?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

                }
            })
    }

    override fun onClick(position: Int, model: SuccesSchedulCategory.Result) {
        scd_name = model.name
        scd_id = model.id
        binding.nameReward.setText(scd_name)
        dialog.dismiss()
    }

}