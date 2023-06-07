package com.app.mndalakanm.model

data class SucessRewardList(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
) : java.io.Serializable {

    data class Result(
        val child_id: String,
        val child_image: String,
        val child_name: String,
        val date_time: String,
        val id: String,
        val name: String,
        val parent_id: String,
        val parent_image: String,
        val parent_name: String,
        val status: String,
        val time: String,
        val time_ago: String
    ) : java.io.Serializable
}