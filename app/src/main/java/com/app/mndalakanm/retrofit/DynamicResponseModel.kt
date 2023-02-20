package com.app.mndalakanm.retrofit

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody

class DynamicResponseModel {
    var apiName = ""
    var errorMessage = ""
    var jsonObject: ResponseBody? = null
    var jsonArray: JsonArray? = null
    var code: Int = 0

    constructor(apiName: String, jsonObject: ResponseBody, code: Int) {
        this.apiName = apiName
        this.jsonObject = jsonObject
        this.code = code
    }

    constructor(apiName: String, jsonObject: JsonObject, code: Int, errorMessage: String)
}