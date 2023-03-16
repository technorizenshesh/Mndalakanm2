package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessChildrReqTime
import com.app.mndalakanm.utils.ChildRequestListClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemRequestListBinding


class AdapterChildRequestsList(val mContext: Context, var transList:
ArrayList<SuccessChildrReqTime.Result>?, val listener: ChildRequestListClickListener
) : RecyclerView.Adapter<AdapterChildRequestsList.TransViewHolder>() {
    lateinit var sharedPref: SharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemRequestListBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
            R.layout.item_request_list, parent, false)
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        var data: SuccessChildrReqTime.Result = transList!!.get(position)
        holder.binding.minuts.text = " +"+data.plus_time+" ms"
        holder.binding.type.text = data.childdetails.dateTime
        /* Glide.with(mContext).load(data.timer).placeholder(R.drawable.ic_baseline_image_search_)
             .error(R.drawable.ic_baseline_broken_image_)
             .into(holder.binding.screen);*/

        holder.binding.ignore.setOnClickListener {
             listener.onClick(position, data,"Rejected")
         }
        holder.binding.accept.setOnClickListener {
             listener.onClick(position, data,"Accepted")
         }
    }

    override fun getItemCount(): Int {
        val limit = 5
        return if (transList == null) 0 else Math.min(transList!!.size, limit)
        // return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemRequestListBinding) :
        RecyclerView.ViewHolder(binding.root)

}