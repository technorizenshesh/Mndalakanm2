package com.app.mndalakanm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SucessRewardList
import com.app.mndalakanm.utils.ParentRewardListClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ItemParentRewardBinding


class AdapterChildRewardList(
    val mContext: Context,
    var transList: ArrayList<SucessRewardList.Result>?, val listener: ParentRewardListClickListener
) : RecyclerView.Adapter<AdapterChildRewardList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemParentRewardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_parent_reward, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, @SuppressLint("RecyclerView") position: Int) {

        var data: SucessRewardList.Result = transList!!.get(position)
        holder.binding.nameTxt.text = data.name
        holder.binding.timeTxt.text = "+ "+data.time+mContext.getString(R.string.min)
         if(data.status =="Active")  holder.binding.switchActive.isChecked= true
        else  holder.binding.switchActive.isChecked= false
        holder.binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.onClick(position, data, "Active", "1")
            } else {
                listener.onClick(position, data, "InActive", "1")
            }
        }
        /*Glide.with(mContext).load(data.image)
            .error(R.drawable.child_icon)
            .placeholder(R.drawable.child_icon)
            .into(holder.binding.ivCar)*/

        holder.binding.rewardBtn.setOnClickListener {
            listener.onClick(position, data,"no","0")

        }
    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemParentRewardBinding) :
        RecyclerView.ViewHolder(binding.root)

}