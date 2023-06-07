package com.app.mndalakanm.model

data class SuccessChildRemainTime(
    val message: String,
    val result: ChildRemainTime,
    val status: String
) : java.io.Serializable {

    data class ChildRemainTime(
        val address: String,
        val age: String,
        val child_image: String,
        val child_name: String,
        val date_time: String,
        val difference_new: Long,
        val final_time: String,
        val id: String,
        val image: String,
        val lat: String,
        val lon: String,
        val mobile_id: String,
        val name: String,
        val pairing_code: String,
        val parent_id: String,
        val parent_image: String,
        val parent_name: String,
        val register_id: String,
        val status: String,
        val time_ago: String,
        val timer: String
    ) : java.io.Serializable
}