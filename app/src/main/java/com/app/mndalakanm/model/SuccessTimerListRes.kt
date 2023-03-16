package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName


data class SuccessTimerListRes(
    @SerializedName("result") var result: ArrayList<TimerList> = arrayListOf(),
    @SerializedName("message") var message: String? = null,
    @SerializedName("final_time") var final_time: String = "0",
    @SerializedName("status") var status: String? = null
) : java.io.Serializable {
    data class TimerList(
        @SerializedName("id") var id: String? = null,
        @SerializedName("parent_id") var parentId: String? = null,
        @SerializedName("child_id") var childId: String? = null,
        @SerializedName("timer") var timer: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("date_time") var dateTime: String? = null,
        @SerializedName("parent_name") var parentName: String? = null,
        @SerializedName("parent_image") var parentImage: String? = null,
        @SerializedName("child_name") var childName: String? = null,
        @SerializedName("child_image") var childImage: String? = null,
        @SerializedName("time_ago") var timeAgo: String? = null

    ) : java.io.Serializable

}
