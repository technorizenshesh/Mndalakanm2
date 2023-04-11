package com.app.mndalakanm.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SuccessChildHistory (
    @Expose
    @SerializedName("result")
    val result: List<Result>,
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
        @SerializedName("parent_id")
        val parentId: String,
        @Expose
        @SerializedName("child_id")
        val childId: String,
        @Expose
        @SerializedName("timer")
        val timer: String,
        @Expose
        @SerializedName("final_time")
        val finalTime: String,
        @Expose
        @SerializedName("time_zone")
        val timeZone: String,
        @Expose
        @SerializedName("status")
        val status: String,
        @Expose
        @SerializedName("date")
        val date: String,
        @Expose
        @SerializedName("time")
        val time: String,
        @Expose
        @SerializedName("date_time")
        val dateTime: String,
        @Expose
        @SerializedName("parent_name")
        val parentName: String,
        @Expose
        @SerializedName("parent_image")
        val parentImage: String,
        @Expose
        @SerializedName("child_name")
        val childName: String,
        @Expose
        @SerializedName("child_image")
        val childImage: String,
        @Expose
        @SerializedName("time_ago")
        val timeAgo: String
    ):java.io.Serializable
}
