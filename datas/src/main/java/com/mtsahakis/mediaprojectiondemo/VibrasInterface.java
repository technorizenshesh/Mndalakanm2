package com.mtsahakis.mediaprojectiondemo;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface VibrasInterface {
    @Multipart
    @POST("add_screenshot")
    Call<ResponseBody> uploadSelfie(
            @Part("parent_id") RequestBody parent_id,
            @Part("child_id") RequestBody child_id,
            @Part("image") RequestBody image);
    @Multipart
    @POST("take_child_screenshot")
    Call<ResponseBody> take_child_screenshot(
            @Part("parent_id") RequestBody parent_id,
            @Part("child_id") RequestBody child_id,
            @Part("image") RequestBody image);
    @FormUrlEncoded
    @POST("request_remove_lockdown")
    Call<ResponseBody> request_remove_lockdown(@FieldMap Map<String, String> stringMap);

}