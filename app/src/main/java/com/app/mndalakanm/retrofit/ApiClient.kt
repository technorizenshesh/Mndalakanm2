package com.app.mndalakanm.retrofit

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        var BASE_URL: String = ApiConstant.BASE_URL
        private var retrofit: Retrofit? = null
        private val httpClient = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()

        fun getClient(context: Context): Retrofit? {
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = httpClient.addInterceptor(interceptor)
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(220, TimeUnit.SECONDS)
                .writeTimeout(220, TimeUnit.SECONDS)
                .build()
            val okHttpClient = OkHttpClient().newBuilder()
                .cache(
                    Cache(
                        File(context.cacheDir, "http-cache"),
                    50L * 1024L * 1024L)
                ) // 10 MiB
                .addNetworkInterceptor(CacheInterceptor())
                .addNetworkInterceptor(ForceCacheInterceptor())
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(220, TimeUnit.SECONDS)
                .writeTimeout(220, TimeUnit.SECONDS)
                .build();



            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}