package com.app.mndalakanm.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mndalakanm.model.SuccesChatListRes
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm.utils.Constant


class AdapterSupportChat(
    val mContext: Context,
    var transList: ArrayList<SuccesChatListRes.Result>?
) : RecyclerView.Adapter<AdapterSupportChat.TransViewHolder>() {
    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        sharedPref = SharedPref(mContext)
        val layoutResId = if (viewType == 0) R.layout.incoming_message_layout else R.layout.outgoing_message_layout
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        Log.e("TAG", "getItemViewType:  viewType-- "+viewType)
        return TransViewHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        val message = transList?.get(position)
        sharedPref = SharedPref(mContext)
        Log.e("TAG", "getItemViewType: senderId-- "+message?.senderId )
        Log.e("TAG", "getItemViewType:  USER_ID -- "+sharedPref.getStringValue(Constant.USER_ID))
        return if (message?.senderId==sharedPref.getStringValue(Constant.USER_ID)) 1 else 0
    }
    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: SuccesChatListRes.Result = transList!!.get(position)
        try {
     holder.bind(data)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val messageTextView = if (itemViewType == 0) itemView.findViewById<TextView>(R.id.userTextSHow) else itemView.findViewById<TextView>(R.id.userTextSHow)
           val timeTextView = if (itemViewType == 0) itemView.findViewById<TextView>(R.id.usertime) else itemView.findViewById<TextView>(R.id.usertime)
        fun bind(message: SuccesChatListRes.Result) {
            messageTextView.text = message.chatMessage
            timeTextView.text = message.timeAgo
            // bind other views as necessary
        }
    }
}