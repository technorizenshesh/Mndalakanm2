package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.utils.ChildClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ItemChildBinding


class AdapterChildList(
    val mContext: Context,
    var transList: ArrayList<ChildData>?, val listener: ChildClickListener
) : RecyclerView.Adapter<AdapterChildList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemChildBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_child, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: ChildData = transList!!.get(position)
        holder.binding.tvCarName.text = data.name
        holder.binding.status.text = data.status
        holder.binding.timeAgo.text = data.timeAgo
        /*Glide.with(mContext).load(data.image)
            .error(R.drawable.child_icon)
            .placeholder(R.drawable.child_icon)
            .into(holder.binding.ivCar)*/

        holder.binding.viewDetails.setOnClickListener {
            listener.onClick(position, data,"1")
            /*  for (i in 0 until transList!!.size){
                transList?.get(i)?.isSelected=false
            }
            transList?.get(position)?.isSelected=true
            notifyDataSetChanged()

        }*/
        }
        holder.binding.deleteChild.setOnClickListener {
            listener.onClick(position, data,"2")
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

    class TransViewHolder(var binding: ItemChildBinding) :
        RecyclerView.ViewHolder(binding.root)

}