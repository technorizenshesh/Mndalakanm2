package com.app.mndalakanm.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.WeekDays
import com.app.mndalakanm.utils.DayClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemDayBinding
import com.techno.mndalakanm.databinding.ItemDaysListBinding
import java.text.SimpleDateFormat
import java.util.*


class AdapterScdDayList(
    val mContext: Context,
    var transList: Array<String>
) : RecyclerView.Adapter<AdapterScdDayList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemDayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_day, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

      //  val dateString = sdf2.format()
        val dateString : String= transList[position]
         holder.binding.day.text = dateString[0].toString()
    }

    override fun getItemCount(): Int {
        return transList.size
    }

    class TransViewHolder(var binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root)

}