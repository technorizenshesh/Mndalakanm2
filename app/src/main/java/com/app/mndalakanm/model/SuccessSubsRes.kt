package com.app.mndalakanm.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


/**
 * Created by Ravindra Birla on 08,February,2023
 */
 data  class SuccessSubsRes(
 @SerializedName("result"  ) var result  : ArrayList<SubsRes> = arrayListOf(),
 @SerializedName("message" ) var message : String?           = null,
 @SerializedName("status"  ) var status  : String?           = null

) {
  data class SubsRes( @SerializedName("id"            ) var id           : String? = null,
                      @SerializedName("name"          ) var name         : String? = null,
                      @SerializedName("total_price"   ) var totalPrice   : String? = null,
                      @SerializedName("for_month"     ) var forMonth     : String? = null,
                      @SerializedName("monthly_price" ) var monthlyPrice : String? = null,
                      @SerializedName("description"   ) var description  : String? = null,
                      @SerializedName("sort_order"    ) var sortOrder    : String? = null,
                      @SerializedName("status"        ) var status       : String? = null,
                      @SerializedName("date_time"     ) var dateTime     : String? = null)
   : Parcelable {
   constructor(parcel: Parcel) : this() {
   }

   override fun writeToParcel(parcel: Parcel, flags: Int) {

   }

   override fun describeContents(): Int {
    return 0
   }

   companion object CREATOR : Parcelable.Creator<SubsRes> {
    override fun createFromParcel(parcel: Parcel): SubsRes {
     return SubsRes(parcel)
    }

    override fun newArray(size: Int): Array<SubsRes?> {
     return arrayOfNulls(size)
    }
   }

  }
}