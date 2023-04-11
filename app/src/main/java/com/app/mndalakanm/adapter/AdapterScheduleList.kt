package com.app.mndalakanm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccessScheduleTime
import com.app.mndalakanm.utils.ParentScheduleListClickListener
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ItemParentSechduleBinding
import com.app.mndalakanm.utils.Constant


class AdapterScheduleList(
    val mContext: Context,
    var transList: ArrayList<SuccessScheduleTime.Result>?, val listener: ParentScheduleListClickListener
) : RecyclerView.Adapter<AdapterScheduleList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemParentSechduleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_parent_sechdule, parent, false
        )
        sharedPref = SharedPref(mContext)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, @SuppressLint("RecyclerView") position: Int) {

        var data: SuccessScheduleTime.Result = transList!!.get(position)
        holder.binding.timeName.text = data. category
        holder.binding.timeSlot.text = data.start_time+mContext.getString(R.string.to)+data.end_time
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            holder.binding.switchActive.isClickable= false
        }
         if(data.status =="Active") {
             holder.binding.switchActive.isChecked= true
        //     holder.binding.background.background=(getDrawable(mContext,R.drawable.desible_bg))

         }
         else{
             holder.binding.background.background=(getDrawable(mContext,R.drawable.desible_bg))
             holder.binding.switchActive.isChecked= false}
        holder.binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.onClick(position, data, "Active", "1")
            } else {
                listener.onClick(position, data, "Deactive", "1")
            }
        }

         if (data.sunday =="1"){
             holder.binding.ss.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.saturday =="1"){
             holder.binding.st.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.monday =="1"){
             holder.binding.m.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.tuesday =="1"){
             holder.binding.t.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.thursday =="1"){
             holder.binding.th.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.friday =="1"){
             holder.binding.fr.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }
        if (data.wednesday =="1"){
             holder.binding.w.background = getDrawable(mContext,R.drawable.background_edit_ovel)
         }


    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemParentSechduleBinding) :
        RecyclerView.ViewHolder(binding.root)

}


