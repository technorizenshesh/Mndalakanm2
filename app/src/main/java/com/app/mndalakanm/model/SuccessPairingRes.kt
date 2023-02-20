package  com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName


 data class SuccessPairingRes(

    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null

)

data class Result(

    @SerializedName("id") var id: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("otp") var otp: String? = null,
    @SerializedName("register_id") var registerId: String? = null,
    @SerializedName("social_id") var socialId: String? = null,
    @SerializedName("pairing_code") var pairingCode: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("date_time") var dateTime: String? = null

)