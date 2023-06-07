package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
data class SuccesChatListRes (
    @Expose
    @SerializedName("result")
    val result: ArrayList<Result>,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("status")
    val status: String
):java.io.Serializable {
    data class Result(
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
        @SerializedName("receiver_status")
        val receiverStatus: String,
        @Expose
        @SerializedName("date")
        val date: String,
        @Expose
        @SerializedName("time_ago")
        val timeAgo: String,
        @Expose
        @SerializedName("result")
        val result: String,
        @Expose
        @SerializedName("sender_detail")
        val senderDetail: SenderDetail,
        @Expose
        @SerializedName("receiver_detail")
        val receiverDetail: ReceiverDetail
    ):java.io.Serializable{
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }

        override fun toString(): String {
            return super.toString()
        }
    }

    data class ReceiverDetail(
      /*  @Expose
        @SerializedName("id")
        val id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("email")
        val email: String,
        @Expose
        @SerializedName("password")
        val password: String,
        @Expose
        @SerializedName("image")
        val image: String,
        @Expose
        @SerializedName("type")
        val type: String,*/
        @Expose
        @SerializedName("receiver_image")
        val receiverImage: String
    )

    data class SenderDetail(
        @Expose
        @SerializedName("sender_image")
        val senderImage: String
    )
}
