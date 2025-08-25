package com.app.bookfinder.data.remote

import com.app.bookfinder.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = BuildConfig.BASE_URL
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private fun createOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "BookFinder/1.0.0 (https://github.com/rikoarik/bookfinder; rikoarik04@gmail.com)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        
        return if (BuildConfig.ENABLE_LOGGING) {
            client.newBuilder().addInterceptor(loggingInterceptor).build()
        } else {
            client
        }
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val openLibraryApi: OpenLibraryApi = retrofit.create(OpenLibraryApi::class.java)
}
