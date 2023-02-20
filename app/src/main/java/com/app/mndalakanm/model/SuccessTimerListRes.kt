package com.app.mndalakanm.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class SuccessTimerListRes (
 @SerializedName("result"  ) var result  : ArrayList<Result> = arrayListOf(),
 @SerializedName("message" ) var message : String?           = null,
 @SerializedName("final_time" ) var final_time : String          = "0",
 @SerializedName("status"  ) var status  : String?           = null
 ){
 data class Result (
  @SerializedName("id"           ) var id          : String? = null,
  @SerializedName("parent_id"    ) var parentId    : String? = null,
  @SerializedName("child_id"     ) var childId     : String? = null,
  @SerializedName("timer"        ) var timer       : String? = null,
  @SerializedName("status"       ) var status      : String? = null,
  @SerializedName("date_time"    ) var dateTime    : String? = null,
  @SerializedName("parent_name"  ) var parentName  : String? = null,
  @SerializedName("parent_image" ) var parentImage : String? = null,
  @SerializedName("child_name"   ) var childName   : String? = null,
  @SerializedName("child_image"  ) var childImage  : String? = null,
  @SerializedName("time_ago"     ) var timeAgo     : String? = null

 ):Parcelable {
  constructor(parcel: Parcel) : this(
   parcel.readString(),
   parcel.readString(),
   parcel.readString(),
   parcel.readString(),
   parcel.readString(),
   parcel.readString(),
   parcel.readString()
  ) {
  }

  override fun describeContents(): Int {
   TODO("Not yet implemented")
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
   TODO("Not yet implemented")
  }

  companion object CREATOR : Parcelable.Creator<Result> {
   override fun createFromParcel(parcel: Parcel): Result {
    return Result(parcel)
   }

   override fun newArray(size: Int): Array<Result?> {
    return arrayOfNulls(size)
   }
  }
 }
}
