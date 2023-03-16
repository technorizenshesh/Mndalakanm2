package com.app.mndalakanm.model

data class SuccessChildPlacesRes(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):java.io.Serializable {

    data class Result(
        val address: String,
        val child_id: String,
        val child_image: String,
        val child_name: Any,
        val date_time: String,
        val enter_notification: String,
        val exit_notification: String,
        val id: String,
        val lat: String,
        val lon: String,
        val name: String,
        val parent_id: String,
        val parent_image: String,
        val parent_name: String,
        val range: String,
        val status: String,
        val time_ago: String,
        val type: String
    )
}