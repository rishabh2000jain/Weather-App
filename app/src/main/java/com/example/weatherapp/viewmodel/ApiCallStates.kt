package com.example.weatherapp.viewmodel

data class ApiCallStates<T>(
    val loading: Boolean = false,
    val failed: Boolean = false,
    val success: Boolean = false,
    val data:T?=null
)
