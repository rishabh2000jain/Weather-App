package com.example.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.CommonResponseModel
import com.example.weatherapp.data.model.GetWeatherData
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepo
import com.example.weatherapp.data.repository.WeatherRepoImpl
import com.example.weatherapp.util.RetrofitClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherDataViewModel():ViewModel() {
    private val weatherDataStates:MutableLiveData<ApiCallStates<WeatherData>> = MutableLiveData()
    private val weatherApi = RetrofitClientProvider.instance()?.createApiService(WeatherApi::class.java)
    private var weatherRepo:WeatherRepo? = weatherApi?.let { WeatherRepoImpl(it) }

    fun weatherDataStates():LiveData<ApiCallStates<WeatherData>>{
        weatherDataStates.value = ApiCallStates(loading = true)
        return weatherDataStates
    }

    fun getWeatherData(getWeatherData:GetWeatherData){
        weatherDataStates.value = ApiCallStates(loading = true)
        viewModelScope.launch {
            val response:CommonResponseModel<WeatherData>? =  withContext(Dispatchers.IO){
                return@withContext weatherRepo?.getWeatherReports(weatherData = getWeatherData)
            }
            if(response?.success == true){
                weatherDataStates.value = ApiCallStates(success = true, data = response.data)
            }else{
                weatherDataStates.value = ApiCallStates(failed = true)
            }
        }
    }

}