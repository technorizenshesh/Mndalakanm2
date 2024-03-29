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
    @POST("get_redeemed_plan")
    fun get_redeem_code_plan(@FieldMap params: Map<String, String>): Call<SuccessRedeemCodeRes>

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
    fun get_child_profile(@FieldMap params: Map<String, String>): Call<SuccessChildProfile>

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
    @POST("add_child_apps")
    fun add_child_apps(
        @Part("parent_id") user_id: RequestBody,
        @Part("child_id") child_id: RequestBody,
        @Part("appid") appid: RequestBody,
        @Part("name") name: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<ResponseBody>

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
    @POST("redeem_code")
    fun redeem_code(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("purchase_plan")
    fun purchase_plan(@FieldMap params: Map<String, String>): Call<ResponseBody>

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
    @POST("add_child_location")
    fun add_child_location(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_screenshot")
    fun get_screenshot(@FieldMap params: Map<String, String>): Call<SuccessScreenshotRes>

    @FormUrlEncoded
    @POST("get_child_screenshot")
    fun get_child_screenshot(@FieldMap params: Map<String, String>): Call<SuccessScreenshotRes>

    @FormUrlEncoded
    @POST("get_child_active_reward")
    fun get_child_timer(@FieldMap params: Map<String, String>): Call<SuccessTimerListRes>

    @FormUrlEncoded
    @POST("add_child_timer")
    fun add_child_timer(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("set_pincode")
    fun set_pincode(@FieldMap params: Map<String, String>)
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("plus_time_request")
    fun plus_time_request(@FieldMap params: Map<String, String>)
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("send_child_notification")
    fun send_child_notification(@FieldMap params: Map<String, String>)
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_child_remaining_time")
    fun get_child_remaining_time(@FieldMap params: Map<String, String>):
            Call<SuccessChildRemainTime>

    @FormUrlEncoded
    @POST("get_weekday_time")
    fun get_weekday_time(@FieldMap params: Map<String, String>):
            Call<SuccessChildDailyLimitsList>

    @FormUrlEncoded
    @POST("get_child_location")
    suspend fun get_child_location(@FieldMap params: Map<String, String>):
            Call<SuccessChildlocation>

    @FormUrlEncoded
    @POST("get_plus_time_request")
    fun get_plus_time_request(
        @FieldMap params: Map<String, String>
    )
            : Call<SuccessChildrReqTime>

    @FormUrlEncoded
    @POST("add_reward")
    fun add_reward(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("accept_reject_time_request")
    fun accept_reject_time_request(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_lockdown_mode")
    fun update_lockdown_mode(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("add_update_weekday_time")
    fun add_update_weekday_time(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_reward_list")
    fun update_reward_list(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("rewards_request")
    fun rewards_request(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("accept_reject_reward_request")
    fun accept_reject_reward_request(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("add_place")
    fun add_place(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("add_schedul_time")
    fun add_lockdown_time(
        @FieldMap params: Map<String, String>
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_schedul_time")
    fun update_schedul_time(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("delete_child")
    fun delete_child(
        @FieldMap params: Map<String, String>
    )
            : Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_reward_list")
    fun get_reward_list(
        @FieldMap params: Map<String, String>
    )
            : Call<SucessRewardList>

    @FormUrlEncoded
    @POST("get_child_reward_list")
    fun get_child_reward_list(
        @FieldMap params: Map<String, String>
    )
            : Call<SucessRewardList>

    @FormUrlEncoded
    @POST("get_schedul_time")
    fun get_child_schedule_list(
        @FieldMap params: Map<String, String>
    )
            : Call<SuccessScheduleTime>

    @FormUrlEncoded
    @POST("get_reward_request")
    fun get_reward_request(
        @FieldMap params: Map<String, String>
    )
            : Call<SuccessChildrRewardReqTime>

    @FormUrlEncoded
    @POST("get_place")
    fun get_place(
        @FieldMap params: Map<String, String>
    )
            : Call<SuccessChildPlacesRes>

    @FormUrlEncoded
    @POST("get_schedul_category")
    fun get_schedul_category(
        @FieldMap params: Map<String, String>
    ): Call<SuccesSchedulCategory>

    @FormUrlEncoded
    @POST("insert_chat")
    fun insert_chat(
        @FieldMap params: Map<String, String>
    ): Call<SuccesInsertChatRes>

    @FormUrlEncoded
    @POST("get_chat")
    fun getChat(
        @FieldMap params: Map<String, String>
    ): Call<SuccesChatListRes>

    @FormUrlEncoded
    @POST("get_faqs")
    fun get_faqs(
        @FieldMap params: Map<String, String>
    ): Call<successFAQres>

    @FormUrlEncoded
    @POST("delete_parent")
    fun delete_parent(
        @FieldMap params: Map<String, String>
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_child_plus_time_history")
    fun get_child_plus_time_history(
        @FieldMap params: Map<String, String>
    ): Call<SuccessChildHistory>
    @FormUrlEncoded
    @POST("change_language")
    fun change_language(
        @FieldMap params: Map<String, String>
    ): Call<SuccessChildHistory>
    @FormUrlEncoded
    @POST("get_child_apps")
    fun get_child_apps(
        @FieldMap params: Map<String, String>
    ): Call<SuccessChildApps>
   @FormUrlEncoded
    @POST("remove_child_apps")
    fun remove_child_apps(
        @FieldMap params: Map<String, String>
    ): Call<ResponseBody>

}