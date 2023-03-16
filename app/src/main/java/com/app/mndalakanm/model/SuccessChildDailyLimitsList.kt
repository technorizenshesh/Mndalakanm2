package com.app.mndalakanm.model

data class SuccessChildDailyLimitsList(
    val message: String,
    val result: Result,
    val status: String
){
data class Result(
    val child_id: String,
    val friday: String,
    val id: String,
    val monday: String,
    val parent_id: String,
    val saturday: String,
    val sunday: String,
    val thursday: String,
    val tuesday: String,
    val wednesday: String
)
}