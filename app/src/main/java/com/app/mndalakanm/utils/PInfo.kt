package com.app.mndalakanm.utils

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.util.Log
import com.app.mndalakanm.Mndalakanm


data class PInfo (
     var id:String = "",
     var appname: String = "",
     var pname: String = "",
     var versionName: String = "",
     var versionCode: String = "",
     var icon: String= "",
     var cat: String = ""):java.io.Serializable