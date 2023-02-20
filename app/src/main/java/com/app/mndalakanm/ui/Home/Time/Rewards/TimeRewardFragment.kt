package com.app.mndalakanm.ui.Home.Time.Rewards

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentTimeRewardBinding

class TimeRewardFragment : Fragment() {


    lateinit var binding: FragmentTimeRewardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_reward, container, false)
        binding.btnAdd.setOnClickListener {
            addDailyTime()
        }

        return binding.root
    }

    private fun addDailyTime() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.getAttributes()?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.add_time_reward_dialog)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.getWindow()!!
        lp.copyFrom(window.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(lp)
        val no_btn: TextView = dialog.findViewById(R.id.no_btn)
        val yes_btn: TextView = dialog.findViewById(R.id.yes_btn)
        no_btn.setOnClickListener { v1: View? -> dialog.dismiss() }
        yes_btn.setOnClickListener { v1: View? ->
            dialog.dismiss()
        }
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }

}