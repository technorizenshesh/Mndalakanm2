package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.utils.ScreenShotClickListener
import com.app.mndalakanm.utils.SharedPref
import com.bumptech.glide.Glide
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemScreenshotsBinding


class AdapterScreenshotList(
    val mContext: Context,
    var transList: ArrayList<SuccessScreenshotRes.ScreenshotList>?,
    val listener: ScreenShotClickListener
) : RecyclerView.Adapter<AdapterScreenshotList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemScreenshotsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_screenshots, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        var data: SuccessScreenshotRes.ScreenshotList = transList!!.get(position)
        // holder.binding.name.text = data.name
        holder.binding.desc.text = data.timeAgo
        Glide.with(mContext).load(data.image).placeholder(R.drawable.ic_baseline_image_search_)
            .error(R.drawable.ic_baseline_broken_image_)
            .into(holder.binding.screen)

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
        val limit = 5
        return if (transList == null) 0 else Math.min(transList!!.size, limit)


        // return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemScreenshotsBinding) :
        RecyclerView.ViewHolder(binding.root)

}