package com.app.mndalakanm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.R
import com.app.mndalakanm.databinding.ItemBlockedAppBinding
import com.app.mndalakanm.model.SuccessChildApps
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.OnBlockedAppListItemClickListener
import com.app.mndalakanm.utils.SharedPref
import com.bumptech.glide.Glide


class AdapterBlockedAppList(
    val mContext: Context,
    var transList: ArrayList<SuccessChildApps.Result>?,
    private val listener: OnBlockedAppListItemClickListener
) : RecyclerView.Adapter<AdapterBlockedAppList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        val binding: ItemBlockedAppBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_blocked_app, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        val data: SuccessChildApps.Result = transList?.get(position)!!
        var show = false
        Log.e("TAG", "onBindViewHolder: "+data.toString() )
        holder.binding.tvCarName.text = data.appid
        holder.binding.timeAgo.text = data.name
        Glide.with(mContext).load(data.image).into(holder.binding.ivCar)
         if (data.status =="Active"){ holder.binding.statusImage.setImageDrawable(mContext.getDrawable(R.drawable.unlocked_state)) }
         else{
             holder.binding.statusImage.setImageDrawable(mContext.getDrawable(R.drawable.locked_state))
         }

holder.binding.statusImage.setOnClickListener {
     if (sharedPref.getStringValue(Constant.USER_TYPE)=="child"){
         return@setOnClickListener
     }else{
    listener.onClick(position, data, )}

}

    }


    override fun getItemCount(): Int {
        if (transList == null) return 0
        return transList?.size!!
    }

    fun notifyAdapter(newsList: ArrayList<SuccessChildApps.Result>) {
        transList = newsList
        notifyDataSetChanged()
    }

    class TransViewHolder(var binding: ItemBlockedAppBinding) :
        RecyclerView.ViewHolder(binding.root)

}