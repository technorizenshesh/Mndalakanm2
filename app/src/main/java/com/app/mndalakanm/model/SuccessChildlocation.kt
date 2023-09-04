package com.app.mndalakanm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuccessChildlocation(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
) : Parcelable {
    @Parcelize
    data class Result(
        val address: String,
        val c_date: String,
        val child_id: String,
        val child_image: String,
        val child_name: String,
        val date_time: String,
        val id: String,
        val lat: String,
        val lon: String,
        val parent_id: String,
        val parent_image: String,
        val parent_name: String,
        val status: String,
        val time_ago: String
    ) : Parcelable
}