package com.app.mndalakanm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


  public class SuccessLoginRes : Serializable {

    @SerializedName("result")
    @Expose
    private var result: Result? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getResult(): Result? {
        return result
    }

    fun setResult(result: Result?) {
        this.result = result
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }
    class Result : Serializable {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("first_name")
        @Expose
        var firstName: String? = null

        @SerializedName("mobile")
        @Expose
        var mobile: String? = null

        @SerializedName("otp")
        @Expose
        var otp: String? = null

        @SerializedName("register_id")
        @Expose
        var registerId: String? = null

        @SerializedName("social_id")
        @Expose
        var socialId: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("date_time")
        @Expose
        var dateTime: String? = null

    }
}