package com.app.mndalakanm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.model.successFAQres
import com.app.mndalakanm.utils.ChildClickListener
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ItemChildBinding
import com.techno.mndalakanm.databinding.ItemFaqBinding


class AdapterFAQList(
    val mContext: Context,
    var transList: ArrayList<successFAQres.Result>?
) : RecyclerView.Adapter<AdapterFAQList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemFaqBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_faq, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: successFAQres.Result = transList!!.get(position)
        holder.binding.tvCarName.text = data.question+" ?"
        holder.binding.status.text = data.answer


        holder.binding.tvCarName.setOnClickListener {
           if ( holder.binding.status.visibility==View.VISIBLE)
               holder.binding.status.visibility= View.GONE
            else
               holder.binding.status.visibility=View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root)

}