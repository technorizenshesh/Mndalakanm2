package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName

data class SuccessPairRes(

    @SerializedName("result") var result: Result2? = Result2(),
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null

)

data class Result2(


    @SerializedName("id") var id: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("pairing_code") var pairingCode: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("age") var age: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("date_time") var dateTime: String? = null

)