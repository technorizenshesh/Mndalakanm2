package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName

data class SuccessChildsListRes(

    @SerializedName("result") var result: ArrayList<ChildData> = arrayListOf(),
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null
)
