package com.example.weatherapp

import android.app.Application
import android.content.SharedPreferences
import com.example.weatherapp.data.locale.SharedPreferenceHelper
import com.example.weatherapp.util.AppUtils
import com.example.weatherapp.util.RetrofitClientProvider

class WeatherApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClientProvider.init(BuildConfig.OPEN_WEATHER_MAP_BASEURL)
        SharedPreferenceHelper.getInstance().init(context = applicationContext)
        AppUtils.initConnectivityManager(context = applicationContext)
    }
}