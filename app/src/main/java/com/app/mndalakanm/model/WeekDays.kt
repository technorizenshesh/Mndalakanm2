package com.app.mndalakanm.model

import java.io.Serializable

data class WeekDays(
    var id: String? = null,
    var date: String? = null,
    var day: String? = null,
    var isSelected: Boolean? = null
) : Serializable


