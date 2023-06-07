package com.app.mndalakanm.model

import com.google.gson.annotations.SerializedName

data class HistoryEntry(
    @SerializedName("title") var title: String? = null,
    @SerializedName("url") var url: String? = null,
)


