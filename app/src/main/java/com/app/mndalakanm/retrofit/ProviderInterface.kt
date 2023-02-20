package com.app.mndalakanm.retrofit

import com.app.mndalakanm.model.*
import com.app.mndalakanm.retrofit.ApiConstant.LOGIN
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProviderInterface {
    @FormUrlEncoded
    @POST(LOGIN)
    fun login_signup(@FieldMap params: Map<String, String>): Call<SuccessLoginRes>

    @FormUrlEncoded
    @POST("verify_otp")
    fun verify_otp(@FieldMap params: Map<String, String>): Call<SuccessVerifyOtpRes>

    @FormUrlEncoded
    @POST("generate_pairing_code")
    fun generate_pairing_code(@FieldMap params: Map<String, String>): Call<SuccessPairingRes>

    @FormUrlEncoded
    @POST("pairing_code")
    fun pairing_code(@FieldMap params: Map<String, String>): Call<SuccessPairRes>

    @FormUrlEncoded
    @POST("get_user_profile")
    fun get_user_profile(@FieldMap params: Map<String, String>): Call<SuccessUserProfile>

    @FormUrlEncoded
    @POST("get_child_profile")
    fun get_child_profile(@FieldMap params: Map<String, String>): Call<SuccessPairRes>

    @FormUrlEncoded
    @POST("get_parent_child")
    fun get_parent_child(@FieldMap params: Map<String, String>): Call<SuccessChildsListRes>

    @FormUrlEncoded
    @POST("get_subscription_plan")
    fun get_subscription_plan(@FieldMap params: Map<String, String>): Call<SuccessSubsRes>

    @Multipart
    @POST("add_child")
    fun add_child(
        @Part("child_id") register_id: RequestBody,
        @Part("age") loagen: RequestBody,
        @Part("name") name: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<SuccessAddChildRes>

    @Multipart
    @POST("update_user_profile")
    fun update_user_profile(

        @Part("user_id") user_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<SuccessUserProfile>

    @Multipart
    @POST("add_screenshot")
    fun add_screenshot(
        @Part("parent_id") parent_id: RequestBody,
        @Part("child_id") name: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("purchase_subscription")
    fun addsub(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("privacy_policy")
    fun privacy_policy(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("terms_conditions")
    fun terms_conditions(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_chlid_lat_lon")
    fun update_chlid_lat_lon(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_screenshot")
    fun get_screenshot(@FieldMap params: Map<String, String>): Call<SuccessScreenshotRes>

    @FormUrlEncoded
    @POST("add_child_timer")
    fun add_child_timer(@FieldMap params: Map<String, String>): Call<ResponseBody>
    @FormUrlEncoded
    @POST("set_pincode")
    fun set_pincode(@FieldMap params: Map<String, String>): Call<ResponseBody>
}