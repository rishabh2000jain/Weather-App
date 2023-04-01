package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface WeatherApi {
    @GET(Constants.WeatherDetailPath)
    suspend fun getWeatherDetails(
        @QueryMap params:Map<String,String>
    ): Response<WeatherData>

}