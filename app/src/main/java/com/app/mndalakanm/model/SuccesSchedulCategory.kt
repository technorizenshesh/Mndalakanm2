package com.app.mndalakanm.model

data class SuccesSchedulCategory(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):java.io.Serializable {

    data class Result(
        val date_time: String,
        val id: String,
        val name: String,
        val status: String
    ):java.io.Serializable
}