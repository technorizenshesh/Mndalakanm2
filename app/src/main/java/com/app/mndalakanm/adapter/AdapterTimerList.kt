package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessTimerListRes
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.TimerListClickListener
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ItemTimerListBinding


class AdapterTimerList(
    val mContext: Context,
    var transList: ArrayList<SuccessTimerListRes.Result>?,
    val listener: TimerListClickListener
) : RecyclerView.Adapter<AdapterTimerList.TransViewHolder>() {
    lateinit var sharedPref: SharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemTimerListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_timer_list, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        var data: SuccessTimerListRes.Result = transList!!.get(position)
        holder.binding.minuts.text = "+ "+data.plusTime+"min"
        holder.binding.status.text = data.timeAgo
        holder.binding.type.text = data.status
        /* Glide.with(mContext).load(data.timer).placeholder(R.drawable.ic_baseline_image_search_)
             .error(R.drawable.ic_baseline_broken_image_)
             .into(holder.binding.screen);*/

        /* holder.binding.parentCard.setOnClickListener {
             listener.onClick(position, data)

         }*/
    }

    override fun getItemCount(): Int {
        val limit = 5
        return if (transList == null) 0 else Math.min(transList!!.size, limit)
        // return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemTimerListBinding) :
        RecyclerView.ViewHolder(binding.root)

}