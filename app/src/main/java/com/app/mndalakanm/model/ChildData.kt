package com.app.mndalakanm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Created by Ravindra Birla on 09,February,2023
 */
@Parcelize
data class ChildData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("pairing_code") var pairingCode: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("age") var age: String? = null,
    @SerializedName("register_id") var registerId: String? = null,
    @SerializedName("mobile_id") var mobileId: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("lon") var lon: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("date_time") var dateTime: String? = null,
    @SerializedName("time_ago") var timeAgo: String? = null,
    @SerializedName("address") var address: String? = null

) : Parcelable