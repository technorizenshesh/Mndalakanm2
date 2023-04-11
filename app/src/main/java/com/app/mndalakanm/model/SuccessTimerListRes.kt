package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


data class SuccessTimerListRes(
    @Expose
    @SerializedName("result")
    val result: ArrayList<Result>,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("status")
    val status: String
) {

    data class Result(
        @Expose
        @SerializedName("id")
        val id: String,
        @Expose
        @SerializedName("parent_id")
        val parentId: String,
        @Expose
        @SerializedName("child_id")
        val childId: String,
        @Expose
        @SerializedName("reward_id")
        val rewardId: String,
        @Expose
        @SerializedName("plus_time")
        val plusTime: String,
        @Expose
        @SerializedName("status")
        val status: String,
        @Expose
        @SerializedName("modification_date")
        val modificationDate: String,
        @Expose
        @SerializedName("date_time")
        val dateTime: String,
        @Expose
        @SerializedName("time_ago")
        val timeAgo: String,
        @Expose
        @SerializedName("childdetails")
        val childdetails: Childdetails
    )

    data class Childdetails(
        @Expose
        @SerializedName("id")
        val id: String,
        @Expose
        @SerializedName("parent_id")
        val parentId: String,
        @Expose
        @SerializedName("pairing_code")
        val pairingCode: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("image")
        val image: String,
        @Expose
        @SerializedName("age")
        val age: String,
        @Expose
        @SerializedName("register_id")
        val registerId: String,
        @Expose
        @SerializedName("mobile_id")
        val mobileId: String,
        @Expose
        @SerializedName("address")
        val address: String,
        @Expose
        @SerializedName("lat")
        val lat: String,
        @Expose
        @SerializedName("lon")
        val lon: String,
        @Expose
        @SerializedName("status")
        val status: String,
        @Expose
        @SerializedName("final_time")
        val finalTime: String,
        @Expose
        @SerializedName("time_zone")
        val timeZone: String,
        @Expose
        @SerializedName("lockdown")
        val lockdown: String,
        @Expose
        @SerializedName("date_time")
        val dateTime: String
    )
}