package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SuccessChildApps(
    @Expose
    @SerializedName("result")
    val result: ArrayList<Result>,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("status")
    val status: String
) :java.io.Serializable{
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
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("image")
        val image: String,
        @Expose
        @SerializedName("appid")
        val appid: String,
        @Expose
        @SerializedName("status")
        val status: String,
        @Expose
        @SerializedName("date_time")
        val dateTime: String,
        @Expose
        @SerializedName("time_ago")
        val timeAgo: String
    ):java.io.Serializable
}
