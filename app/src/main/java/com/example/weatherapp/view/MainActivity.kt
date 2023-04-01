package com.example.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.data.model.GetWeatherData
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.util.AppUtils
import com.example.weatherapp.util.Constants
import com.example.weatherapp.viewmodel.WeatherDataViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_CODE: Int = 200
    }

    private lateinit var weatherDataViewModel: WeatherDataViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fuseLocationProvider: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fuseLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        weatherDataViewModel = ViewModelProvider(this)[WeatherDataViewModel::class.java]
        addObserverToWeatherDataState()
        binding.pullToRefresh.setOnRefreshListener() {
            getWeatherData()
        }
    }

    private fun showLoader() {
        binding.loader.visibility = View.VISIBLE
        hideNoDataScreen()
    }

    private fun hideLoader() {
        binding.loader.visibility = View.GONE
    }

    private fun showNoDataScreen(){
        binding.noDataGroup.visibility = View.VISIBLE
    }
    private fun hideNoDataScreen(){
        binding.noDataGroup.visibility = View.GONE
    }

    private fun showWeatherDetailUI() {
        binding.weatherDetailGrp.visibility = View.VISIBLE
    }

    private fun hideWeatherDetailUI() {
        binding.weatherDetailGrp.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        getWeatherData()
    }

    @SuppressLint("SetTextI18n")
    private fun populateDataInUI(data: WeatherData) {
        val weather = data.weather.first();
        val icon: Int? = Constants.weatherIconMap[weather.icon]
        if (icon != null) {
            binding.weatherImg.setImageDrawable(ContextCompat.getDrawable(this, icon))
        }
        binding.weatherConditionDescTxt.isSelected = true
        binding.weatherConditionDescTxt.text = weather.description

        binding.weatherConditionTitleTxt.text = weather.main
        binding.humidityTempTxt.text = String.format("%.2f", (data.main.temp - 273)) + "°C"
        binding.humidityTxt.text = data.main.humidity.toString() + " per cent"
        binding.sunriseTxt.text =
            SimpleDateFormat.getTimeInstance().format(Date((data.sys.sunrise * 1000)))
        binding.sunsetTxt.text =
            SimpleDateFormat.getTimeInstance().format(Date((data.sys.sunset * 1000)))
        binding.windSpeedTxt.text = data.wind.speed.toString()
        binding.minTempTxt.text = String.format("%.2f", (data.main.temp_min - 273)) + " min"
        binding.maxTempTxt.text = String.format("%.2f", (data.main.temp_max - 273)) + " max"
        binding.cityTxt.text = data.name
        binding.countryTxt.text = data.sys.country

    }

    private fun requestLocationPermission() {
        if (!isPermissionEnabled(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showAlertDialog("Location Permission","Permission is required to get your current location, click ok to turn it on"){
                    openAppSettings()
                }
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_CODE
                )
            }
        } else {
            getWeatherData()
        }
    }

    private fun isPermissionEnabled(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getWeatherData() {
        when{
            !isPermissionEnabled(Manifest.permission.ACCESS_FINE_LOCATION)->{
                requestLocationPermission()
            }
            !AppUtils.gpsIsTurnedOn(context = this)->{
                showAlertDialog("GPS Disabled","GPS service is required to fetch your device location, click ok to turn it on"){
                    openGpsSettings()
                }

            }
            else->{

                lifecycleScope.launch {
                    val location:Location? = withContext(Dispatchers.IO){
                        return@withContext getUsersCurrentLocation()
                    }
                    if(location!=null) {
                        weatherDataViewModel.getWeatherData(GetWeatherData(lat = location.latitude, long = location.longitude))
                    }
                }
            }
        }


    }

    private fun openGpsSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun openAppSettings(){
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } catch (noActivityException: ActivityNotFoundException) {
            Toast.makeText(this@MainActivity, "No app to open settings", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun showAlertDialog(title:String, desc:String, onPositiveActionClick:()->Unit) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(desc)
        dialogBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
           onPositiveActionClick.invoke()
        }

        dialogBuilder.create()
        dialogBuilder.show()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getUsersCurrentLocation(): Location? {
        return if (isPermissionEnabled(Manifest.permission.ACCESS_FINE_LOCATION) && isPermissionEnabled(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            runOnUiThread {
                showLoader()
                hideWeatherDetailUI()
            }
            return try {
                fuseLocationProvider.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null,
                ).await()
            } catch (cancel: CancellationException) {
                runOnUiThread{
                Toast.makeText(this@MainActivity, "Failed to fetch location", Toast.LENGTH_SHORT)
                    .show()
                }
                null
            }finally {
                runOnUiThread {
                    hideLoader()
                }
            }
        } else {
            null
        }

    }

    private fun addObserverToWeatherDataState(){
        weatherDataViewModel.weatherDataStates().observe(this@MainActivity) {
            when {
                it.loading -> {
                    showLoader()
                    hideWeatherDetailUI()
                }
                it.success -> {
                    hideLoader()
                    if (it.data != null) {
                        populateDataInUI(it.data)
                        showWeatherDetailUI()
                    }
                    binding.pullToRefresh.isRefreshing = false
                }
                it.failed -> {
                    hideLoader()
                    hideWeatherDetailUI()
                    binding.pullToRefresh.isRefreshing = false
                    showNoDataScreen()
                }
            }
        }
    }

}
