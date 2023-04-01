package com.example.weatherapp.data.model

data class CommonResponseModel<T>(
    val success: Boolean,
    val data: T?=null,
    val exception: java.lang.Exception?=null,
)
