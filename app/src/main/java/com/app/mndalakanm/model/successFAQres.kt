package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class successFAQres(
    @Expose
    @SerializedName("result")
    val result: ArrayList<Result>,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("status")
    val status: String
):java.io.Serializable{

data class Result(
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("question")
    val question: String,
    @Expose
    @SerializedName("answer")
    val answer: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("date_time")
    val dateTime: String
)
}