package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessSubsRes
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.utils.SubClickListener
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemSubscriptionBinding


class AdapterSubscriptionList(
    val mContext: Context,
    var transList: ArrayList<SuccessSubsRes.SubsRes>?, val listener: SubClickListener
) : RecyclerView.Adapter<AdapterSubscriptionList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemSubscriptionBinding = DataBindingUtil.inflate (
            LayoutInflater.from(mContext), R.layout.item_subscription, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: SuccessSubsRes.SubsRes = transList!!.get(position)
        holder.binding.name.text = data.name
        holder.binding.desc.text = data.description


        holder.binding.parentCard.setOnClickListener {
            listener.onClick(position, data)
            /*  for (i in 0 until transList!!.size){
                transList?.get(i)?.isSelected=false
            }
            transList?.get(position)?.isSelected=true
            notifyDataSetChanged()

        }*/
        }
    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {}

}