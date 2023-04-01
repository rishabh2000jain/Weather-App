package com.example.weatherapp.util

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object AppUtils {
    private var connectivityManager:ConnectivityManager? = null
    fun initConnectivityManager(context: Context){
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    fun isConnectedToInternet():Boolean{
        val network:Network = connectivityManager?.activeNetwork?:return false
        val activeNetwork = connectivityManager?.getNetworkCapabilities(network)?:return false
        return when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)-> true
            else-> false
        }
    }

    fun gpsIsTurnedOn(context: Context):Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


}