package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.locale.SharedPrefKeys
import com.example.weatherapp.data.locale.SharedPreferenceHelper
import com.example.weatherapp.data.model.CommonResponseModel
import com.example.weatherapp.data.model.GetWeatherData
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.util.AppUtils
import com.example.weatherapp.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepoImpl(private val apiService: WeatherApi) : WeatherRepo {
    override suspend fun getWeatherReports(weatherData: GetWeatherData): CommonResponseModel<WeatherData> {
        try {
            val params = mapOf<String,String>(
                Constants.lat to weatherData.lat.toString(),
                Constants.long to weatherData.long.toString(),
                Constants.appId to BuildConfig.OPEN_WEATHER_MAP_API_KEY
            )
            if(!AppUtils.isConnectedToInternet()){
                val data = SharedPreferenceHelper.getInstance().getString(SharedPrefKeys.WEATHER_DATA)
               return  if(data.isNullOrBlank()){
                      CommonResponseModel<WeatherData>(false)
                }else {
                    val gson = Gson()
                    val localeData = gson.fromJson<WeatherData>(data,WeatherData::class.java)
                    if(localeData.coord.lat == String.format("%.4f",weatherData.lat).toDouble() && localeData.coord.lon == String.format("%.4f",weatherData.long).toDouble()){
                        CommonResponseModel<WeatherData>(true,localeData)
                    }else{
                        SharedPreferenceHelper.getInstance().clear()
                        CommonResponseModel<WeatherData>(false,null)
                    }
                }
            }
            val response: Response<WeatherData> = apiService.getWeatherDetails(params)
            return if (response.isSuccessful) {
                SharedPreferenceHelper.getInstance().saveString(SharedPrefKeys.WEATHER_DATA,Gson().toJson(response.body()))
                CommonResponseModel<WeatherData>(response.isSuccessful,response.body())
            } else {
                CommonResponseModel<WeatherData>(response.isSuccessful)
            }
        } catch (exception: HttpException) {
            return  CommonResponseModel<WeatherData>(success = false,exception=exception)
        } catch (ioException: IOException) {
            return  CommonResponseModel<WeatherData>(success = false,exception=ioException)
        }
    }
}