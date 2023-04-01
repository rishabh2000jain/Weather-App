package com.example.weatherapp.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientProvider private constructor() {
    companion object {
        private var RETROFIT_INSTANCE: Retrofit? = null
        private var INSTANCE: RetrofitClientProvider? = null
        fun init(baseUrl: String) {
            synchronized(this) {
                val retrofit: Retrofit = RETROFIT_INSTANCE ?: Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                RETROFIT_INSTANCE = retrofit
                INSTANCE = INSTANCE ?: RetrofitClientProvider()
            }
        }

        fun instance(): RetrofitClientProvider? {
            return INSTANCE
        }
    }
    fun <T> createApiService(service: Class<T>): T? {
        return RETROFIT_INSTANCE?.create(service)
    }

}