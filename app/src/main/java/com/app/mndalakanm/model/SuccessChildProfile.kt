package com.app.mndalakanm.model

data class SuccessChildProfile(
    val message: String,
    val result: Result,
    val status: String
):java.io.Serializable {

    data class Result(
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
        val parentdetails: Parentdetails,
        val register_id: String,
        val status: String,
        val time_zone: String
    ):java.io.Serializable


data class Parentdetails(
    val date_time: String,
    val first_name: String,
    val id: String,
    val image: String,
    val mobile: String,
    val otp: String,
    val pairing_code: String,
    val pin_code: String,
    val register_id: String,
    val social_id: String,
    val status: String,
    val time_zone: String,
    val ud_id: String
):java.io.Serializable
}