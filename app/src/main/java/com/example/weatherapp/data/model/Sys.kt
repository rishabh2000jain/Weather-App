package com.example.weatherapp.data.model

import java.io.Serializable

data class Sys(
    val sunrise: Long,
    val sunset: Long,
    val country: String
): Serializable