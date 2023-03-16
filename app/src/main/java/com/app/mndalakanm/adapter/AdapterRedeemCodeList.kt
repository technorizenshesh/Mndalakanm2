package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessRedeemCodeRes
import com.app.mndalakanm.utils.RedeemCodeClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemRedeemCodeBinding


class AdapterRedeemCodeList(
    val mContext: Context,
    var transList: ArrayList<SuccessRedeemCodeRes.Result>?, val listener: RedeemCodeClickListener
) : RecyclerView.Adapter<AdapterRedeemCodeList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemRedeemCodeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_redeem_code, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: SuccessRedeemCodeRes.PlanDetails = transList!!.get(position).plan_details
        holder.binding.name.text = transList!!.get(position).redeem_code
        holder.binding.desc.text = data.description
        holder.binding.parentCard.setOnClickListener {
            listener.onClick(position, transList!!.get(position))

        }
    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemRedeemCodeBinding) :
        RecyclerView.ViewHolder(binding.root)

}