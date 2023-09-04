package com.app.mndalakanm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SuccessRedeemCodeRes(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
) : Parcelable {
    @Parcelize
    data class Result(
        val admin_approval: String,
        val date_time: String,
        val end_date: String,
        val id: String,
        val parent_id: String,
        val plan_details: PlanDetails,
        val plan_id: String,
        val price: String,
        val redeem_code: String,
        val start_date: String,
        val status: String
    ) : Parcelable

    @Parcelize

    data class PlanDetails(
        val date_time: String,
        val description: String,
        val for_days: String,
        val id: String,
        val name: String,
        val price: String,
        val status: String
    ) : Parcelable
}