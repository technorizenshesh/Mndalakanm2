package com.app.mndalakanm.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccesSchedulCategory
import com.app.mndalakanm.model.WeekDays
import com.app.mndalakanm.utils.DayClickListener
import com.app.mndalakanm.utils.SechduleClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemCatListBinding
import com.techno.mndalakanm.databinding.ItemDaysListBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdapterSecdCatList(
    val mContext: Context,
    var transList: ArrayList<SuccesSchedulCategory.Result>, val listener: SechduleClickListener
) : RecyclerView.Adapter<AdapterSecdCatList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemCatListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_cat_list, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: SuccesSchedulCategory.Result = transList.get(position)
      //  val dateString = sdf2.format()
        holder.binding.date.text = data.name
         holder.binding.root.setOnClickListener{
             listener.onClick(position,data)
         }
    }

    override fun getItemCount(): Int {
        return transList.size
    }

    class TransViewHolder(var binding: ItemCatListBinding) :
        RecyclerView.ViewHolder(binding.root)

}