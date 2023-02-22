package com.mtsahakis.mediaprojectiondemo;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ApiClient {
    public static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .readTimeout(300, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://3tdrive.com/Mndalakanm/webservice/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
