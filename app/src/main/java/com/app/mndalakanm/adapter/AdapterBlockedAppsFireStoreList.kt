package com.app.mndalakanm.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.R
import com.app.mndalakanm.databinding.ItemAppBinding
import com.app.mndalakanm.utils.AppClickListener
import com.app.mndalakanm.utils.PInfo
import com.app.mndalakanm.utils.SharedPref


class AdapterBlockedAppsFireStoreList(
    val mContext: Context,
    var transList: ArrayList<PInfo>, val listener: AppClickListener
) : RecyclerView.Adapter<AdapterBlockedAppsFireStoreList.TransViewHolder>() {
    lateinit var sharedPref: SharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemAppBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_app, parent, false)
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        var data: PInfo = transList.get(position)
        holder.binding.tvCarName.text = data.appname
        holder.binding.timeAgo.text = data.pname


        holder.binding.root.setOnClickListener{
            Log.e("TAG", "onBindViewHolderonBindViewHolder: "+data.toString() )
        }
        try {


        val decodedBytes = Base64.decode(data.icon, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.binding.ivCar.setImageBitmap(bitmap)
        }catch (e:Exception){
            e.printStackTrace()
        }
        holder.binding.editBtn.setOnClickListener {
            holder.binding.ivCar.isDrawingCacheEnabled = true
            holder.run { binding.ivCar.buildDrawingCache() }
            val bitmap: Bitmap = Bitmap.createBitmap(   holder.binding.ivCar.drawingCache)
            holder.binding.ivCar.isDrawingCacheEnabled = false
            listener.onClick(position, data,"2",bitmap) }
    }
    override fun getItemCount(): Int {
        return transList.size
    }
    class TransViewHolder(var binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root)

}