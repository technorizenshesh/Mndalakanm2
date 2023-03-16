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
import com.techno.mndalakanm.databinding.ItemDaysListBinding
import java.text.SimpleDateFormat
import java.util.*


class AdapterDaysList(
    val mContext: Context,
    var transList: Array<WeekDays>, val listener: DayClickListener
) : RecyclerView.Adapter<AdapterDaysList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemDaysListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_days_list, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: WeekDays = transList.get(position)
      //  val dateString = sdf2.format()
        val dateString : String= data.date.toString()
        holder.binding.date.text = dateString.takeLast(2)
        holder.binding.day.text = data.day
        if (data.isSelected == true){
            holder.binding.day.setTextColor(mContext.getColor(R.color.colorPrimary))
        holder.binding.date.setTextColor(mContext.getColor(R.color.colorPrimary))
    }else{
        holder.binding.day.setTextColor(mContext.getColor(R.color.texts))
        holder.binding.date.setTextColor(mContext.getColor(R.color.texts))}
       holder.binding.root.setOnClickListener {
            listener.onClick(position, data)
             for (element in transList){
                element.isSelected=false
            }
            transList[position].isSelected=true
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return transList.size
    }

    class TransViewHolder(var binding: ItemDaysListBinding) :
        RecyclerView.ViewHolder(binding.root)

}