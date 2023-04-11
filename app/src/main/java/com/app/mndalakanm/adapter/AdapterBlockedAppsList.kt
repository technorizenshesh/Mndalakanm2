package com.app.mndalakanm.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.utils.AppClickListener
import com.app.mndalakanm.utils.ChildClickListener
import com.app.mndalakanm.utils.PInfo
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ItemAppBinding
import com.app.mndalakanm
.databinding.ItemChildBinding


class AdapterBlockedAppsList(
    val mContext: Context,
    var transList: ArrayList<PInfo>, val listener: AppClickListener
) : RecyclerView.Adapter<AdapterBlockedAppsList.TransViewHolder>() {
    lateinit var sharedPref: SharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemAppBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_app, parent, false)
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        var data: PInfo = transList.get(position)
        holder.binding.tvCarName.text = data.appname
        holder.binding.timeAgo.text = data.cat
        try {


        val decodedBytes = Base64.decode(data.icon, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.binding.ivCar.setImageBitmap(bitmap)
        }catch (e:Exception){
            e.printStackTrace()
        }
        holder.binding.root.setOnClickListener {
            listener.onClick(position, data,"2") }
    }
    override fun getItemCount(): Int {
        return transList.size
    }
    class TransViewHolder(var binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root)

}