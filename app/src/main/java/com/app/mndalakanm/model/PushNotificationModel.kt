package com.app.mndalakanm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushNotificationModel(
    @SerializedName("message") var message: Message? = Message()
) : Parcelable {
    @Parcelize
    data class Message(
        @SerializedName("result") var result: String? = null,
        @SerializedName("parent_name") var parentName: String? = null,
        @SerializedName("child_id") var childId: Int? = null,
        @SerializedName("child_image") var childImage: String? = null,
        @SerializedName("perent_id") var perentId: String? = null,
        @SerializedName("parent_image") var parentImage: String? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("child_name") var childName: String? = null,
        @SerializedName("key") var key: String? = null

    ) : Parcelable
}


