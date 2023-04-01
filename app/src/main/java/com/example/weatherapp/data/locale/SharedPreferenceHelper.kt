package com.example.weatherapp.data.locale

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper private constructor(){
    private var sharedPreferences:SharedPreferences? = null
    private var sharedPreferencesEditor:SharedPreferences.Editor? = null
    companion object SharedPrefInstanceBuilder{
        private val INSTANCE:SharedPreferenceHelper = SharedPreferenceHelper()

        fun getInstance():SharedPreferenceHelper = INSTANCE

    }

    fun init(context:Context){
        if(sharedPreferences!=null)return;
        sharedPreferences = context.getSharedPreferences("${context.packageName}.MyPref",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
    }

    fun saveString(key:String,value:String){
        sharedPreferencesEditor?.putString(key,value)
        commit()
    }

    fun getString(key:String):String?{
        return sharedPreferences?.getString(key,"")
    }
    fun clear(){
         sharedPreferencesEditor?.clear()
        commit()
    }

    private fun commit(){
        sharedPreferencesEditor?.commit()
    }

}
object SharedPrefKeys{
    const val WEATHER_DATA:String = "KEY_WEATHER_DATA"
}
