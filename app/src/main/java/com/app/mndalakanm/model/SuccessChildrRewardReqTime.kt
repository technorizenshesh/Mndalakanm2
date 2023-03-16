package com.app.mndalakanm.model

data class SuccessChildrRewardReqTime(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):java.io.Serializable {

    data class Result(
        val child_id: String,
        val childdetails: Childdetails,
        val date_time: String,
        val id: String,
        val parent_id: String,
        val plus_time: String,
        val status: String,
        val time_ago: String,
        val time_zone: String
    )

    data class Childdetails(
        val address: String,
        val age: String,
        val date_time: String,
        val final_time: String,
        val id: String,
        val image: String,
        val lat: String,
        val lockdown: String,
        val lon: String,
        val mobile_id: String,
        val name: String,
        val pairing_code: String,
        val parent_id: String,
        val register_id: String,
        val status: String,
        val time_zone: String
    )
}