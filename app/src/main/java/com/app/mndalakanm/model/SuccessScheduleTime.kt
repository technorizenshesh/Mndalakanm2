package com.app.mndalakanm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuccessScheduleTime(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
) :Parcelable {
    @Parcelize
    data class Result(
    val category: String,
    val category_id: String,
    val child_id: String,
    val child_image: String,
    val child_name: String,
    val date_time: String,
    val end_time: String,
    val friday: String,
    val id: String,
    val monday: String,
    val parent_id: String,
    val parent_image: String,
    val parent_name: String,
    val saturday: String,
    val start_time: String,
    val status: String,
    val sunday: String,
    val thursday: String,
    val time_ago: String,
    val tuesday: String,
    val wednesday: String

    ):Parcelable
}