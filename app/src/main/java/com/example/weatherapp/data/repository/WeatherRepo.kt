package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.CommonResponseModel
import com.example.weatherapp.data.model.GetWeatherData
import com.example.weatherapp.data.model.WeatherData

interface WeatherRepo {
    suspend fun getWeatherReports(weatherData: GetWeatherData):CommonResponseModel<WeatherData>
}