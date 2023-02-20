package com.app.mndalakanm.ui.Home.Time.Daily

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentAddDailyTimeBinding


class AddDailyTimeFragment : Fragment() {

    lateinit var binding: FragmentAddDailyTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_daily_time,
            container, false
        )
        binding.btnEdit.setOnClickListener {
            addDailyTime()
        }
        binding.editImage.setOnClickListener {
            addDailyTime()
        }
        return binding.root
    }


    private fun addDailyTime() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.getAttributes()?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.add_time_dialog)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.getWindow()!!
        lp.copyFrom(window.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(lp)
        val no_btn: TextView = dialog.findViewById(R.id.no_btn)
        val yes_btn: TextView = dialog.findViewById(R.id.yes_btn)
        val dialog_haur_picker: NumberPicker = dialog.findViewById(R.id.dialog_haur_picker)
        val dialog_minuts_picker: NumberPicker = dialog.findViewById(R.id.dialog_minuts_picker)
        dialog_haur_picker.maxValue = 23
        dialog_haur_picker.minValue = 0
        dialog_haur_picker.wrapSelectorWheel = false
        dialog_haur_picker.setOnValueChangedListener { dialog_haur_picker, i, i1 ->
            Toast.makeText(requireContext(), "" + i, Toast.LENGTH_SHORT).show()
        }
        dialog_minuts_picker.maxValue = 59
        dialog_minuts_picker.minValue = 0
        dialog_minuts_picker.wrapSelectorWheel = false
        dialog_minuts_picker.setOnValueChangedListener { dialog_haur_picker, i, i1 ->
            Toast.makeText(requireContext(), "" + i, Toast.LENGTH_SHORT).show()
        }
        no_btn.setOnClickListener { v1: View? -> dialog.dismiss() }
        yes_btn.setOnClickListener { v1: View? ->
            dialog.dismiss()
        }
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }

}