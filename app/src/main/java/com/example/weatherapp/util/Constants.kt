package com.example.weatherapp.util

import com.example.weatherapp.R

object Constants {
    const val lat: String = "lat"
    const val long: String = "lon"
    const val appId: String = "appid"
    const val WeatherDetailPath: String = "/data/2.5/weather"
    val weatherIconMap = mapOf<String, Int>(
        "01d" to R.drawable._01d,
        "02d" to R.drawable._02d,
        "03d" to R.drawable._03d,
        "04d" to R.drawable._04d,
        "09d" to R.drawable._09d,
        "10d" to R.drawable._10d,
        "11d" to R.drawable._11d,
        "13d" to R.drawable._13d,
        "50d" to R.drawable._50d,
        "01n" to R.drawable._01n,
        "02n" to R.drawable._02n,
        "03n" to R.drawable._03n,
        "04n" to R.drawable._04n,
        "09n" to R.drawable._09n,
        "10n" to R.drawable._10n,
        "11n" to R.drawable._11n,
        "13n" to R.drawable._13n,
        "50n" to R.drawable._50n,
    )
}