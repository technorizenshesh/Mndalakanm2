package com.app.mndalakanm.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class SuccessScreenshotRes (
 @SerializedName("result"  ) var result  : ArrayList<ScreenshotList> = arrayListOf(),
 @SerializedName("message" ) var message : String?           = null,
 @SerializedName("status"  ) var status  : String?           = null
 ){
 data class ScreenshotList (
  @SerializedName("id"        ) var id       : String? = null,
  @SerializedName("parent_id" ) var parentId : String? = null,
  @SerializedName("child_id"  ) var childId  : String? = null,
  @SerializedName("image"     ) var image    : String? = null,
  @SerializedName("status"    ) var status   : String? = null,
  @SerializedName("date_time" ) var dateTime : String? = null,
  @SerializedName("time_ago"  ) var timeAgo  : String? = null

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

  companion object CREATOR : Parcelable.Creator<ScreenshotList> {
   override fun createFromParcel(parcel: Parcel): ScreenshotList {
    return ScreenshotList(parcel)
   }

   override fun newArray(size: Int): Array<ScreenshotList?> {
    return arrayOfNulls(size)
   }
  }
 }
}
