package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName

data class SuccessAddChildRes (
    @SerializedName("result"  ) var result  : ChildData? = ChildData(),
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("status"  ) var status  : String? = null

)