package com.app.mndalakanm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuccessChildrReqTime(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
): Parcelable{
    @Parcelize
data class Result(
    val child_id: String,
    val childdetails: ChildData,
    val date_time: String,
    val id: String,
    val parent_id: String,
    val plus_time: String,
    val status: String,
    val time_ago: String
): Parcelable
}
