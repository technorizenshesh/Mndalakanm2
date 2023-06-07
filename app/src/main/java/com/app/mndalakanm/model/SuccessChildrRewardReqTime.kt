package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SuccessChildrRewardReqTime(
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
        @SerializedName("time_ago")
        val timeAgo: String
    )
}