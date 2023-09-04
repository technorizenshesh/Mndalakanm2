package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SuccesInsertChatRes (
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("sender_id")
    val senderId: String,
    @Expose
    @SerializedName("receiver_id")
    val receiverId: String,
    @Expose
    @SerializedName("chat_message")
    val chatMessage: String,
    @Expose
    @SerializedName("chat_image")
    val chatImage: String,
    @Expose
    @SerializedName("sender_type")
    val senderType: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("result")
    val result: String
)
