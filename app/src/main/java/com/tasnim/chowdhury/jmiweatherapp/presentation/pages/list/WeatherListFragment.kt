package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.WeatherWorker
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.databinding.FragmentWeatherListBinding
import com.tasnim.chowdhury.jmiweatherapp.domain.model.WeatherModel
import com.tasnim.chowdhury.jmiweatherapp.presentation.adapters.WeatherListAdapter
import com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list.viewModel.WeatherViewModel
import com.tasnim.chowdhury.jmiweatherapp.presentation.service.Notification
import com.tasnim.chowdhury.jmiweatherapp.presentation.service.messageExtra
import com.tasnim.chowdhury.jmiweatherapp.presentation.service.titleExtra
import com.tasnim.chowdhury.jmiweatherapp.util.Constants
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_ID
import com.tasnim.chowdhury.jmiweatherapp.util.Status
import com.tasnim.chowdhury.jmiweatherapp.util.LatLonCallBack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class WeatherListFragment : Fragment() {

    private lateinit var binding: FragmentWeatherListBinding
    private lateinit var weatherAdapter: WeatherListAdapter
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var request : OneTimeWorkRequest
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    lateinit var data : Data
    private val permissionId = 2
    var lat = ""
    var lon = ""
    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentWeatherListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupWorker()

        setupAdapter()
        setupObserver()
        setupClicks()

        createNotificationChannel()
        scheduleIt()
    }

    private fun setupAdapter() {
        weatherAdapter = WeatherListAdapter()
        binding.weatherListRv.adapter = weatherAdapter
        binding.weatherListRv.setHasFixedSize(true)
        binding.weatherListRv.itemAnimator = DefaultItemAnimator()
        binding.weatherListRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        weatherViewModel.fetchWeatherList()
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.weatherState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.weatherListRv.visibility = View.GONE

                        Log.d("WeatherListFragment", "Loading___")
                    }

                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.weatherListRv.visibility = View.VISIBLE

                        it.data?.let {  weatherResponse ->
                            val list: List<WeatherModel> = weatherResponse.list!!.map { response ->
                                WeatherModel(
                                    id = response.id,
                                    feelsLike = response.main.feelsLike,
                                    temp = response.main.temp,
                                    condition = response.weather[0].main,
                                    cityName = response.name,
                                    lat = response.coord.lat,
                                    lon = response.coord.lon,
                                    humidity = response.main.humidity,
                                    windSpeed = response.wind.speed,
                                    maxTemp = response.main.tempMax,
                                    minTemp = response.main.tempMin,
                                    icon = response.weather[0].icon
                                )
                            }

                            Log.d("WeatherListFragment", "$list")
                            weatherAdapter.addWeatherData(list)
                        }
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.weatherListRv.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                        Log.d("WeatherListFragment", "${it.message}")
                    }
                }
            }
        }
    }

    private fun setupClicks() {
        weatherAdapter.itemClick = { weatherData ->
            findNavController().navigate(
                WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailsFragment(weatherData)
            )
        }
    }

    //sssssssss
    private fun setupWorker() {
        getLocation(object : LatLonCallBack {
            override fun onLocationReceived(latitude: String, longitude: String) {
                Log.d("chkGainLocation", "$latitude $longitude 1")
                data = Data.Builder().putString("lat", lat).putString("lon", lon).build()

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .build()

                val periodicWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(1, TimeUnit.MINUTES)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                    "weather_notification",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
            }
        })
    }

    private fun scheduleIt() {
        getLocation(object : LatLonCallBack {
            @SuppressLint("ScheduleExactAlarm")
            override fun onLocationReceived(latitude: String, longitude: String) {
                // Calculate delay until the next scheduled time
                val specificTimeInMillis = calculateSpecificTime()
                val delayMillis = specificTimeInMillis - System.currentTimeMillis()

                if (delayMillis > 0) {
                    val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(activity?.applicationContext, Notification::class.java)
                    alarmIntent.putExtra("lat", latitude)
                    alarmIntent.putExtra("lon", longitude)

                    val pendingIntent = PendingIntent.getBroadcast(
                        activity?.applicationContext,
                        NOTIFICATION_ID,
                        alarmIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    // Schedule the alarm with the calculated delay
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + delayMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + delayMillis,
                            pendingIntent
                        )
                    }
                }
            }
        })
    }

    private fun fetchWeatherDataAndShowNotification(lat: String, lon: String) {
        lifecycleScope.launch {
            try {
                // Collect the flow to get the actual data
                val result = weatherViewModel.fetchCurrentWeatherData(lat, lon).firstOrNull()
                result?.data?.let { currentDTO ->
                    showNotification(lat, lon, currentDTO)
                }
            } catch (e: Exception) {
                // Handle API call errors
                Log.e("WeatherListFragment", "API call error: ${e.message}")
            }
        }
    }

    private fun showNotification(lat: String, lon: String, currentDTO: CurrentDTO) {
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationContent =
            "Latsss: $lat, Lon: $lon\nCity: ${currentDTO.name}\nTemperature: ${currentDTO.main?.temp}°C"

        val notification = NotificationCompat.Builder(requireContext(), Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Weather Details")
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.scater_clouds)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }


    private fun calculateSpecificTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 2) // 1:00 AM
        calendar.set(Calendar.MINUTE, 33)
        calendar.set(Calendar.SECOND, 0)

        val currentTime = LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC) * 1000

        // If the current time is after 1:55 AM, schedule for the next day
        if (currentTime > calendar.timeInMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    private fun createNotificationChannel(){
        val name = Constants.NOTIFICATION_CHANNEL_NAME
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    //ssssssssss

    private fun getLocation(latLonCallback: LatLonCallBack) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    Log.d("chkLocationD", "$location +++")
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            Log.d("chkLocationD", "$list ---")
                            lat = list?.get(0)?.latitude.toString()
                            lon = list?.get(0)?.longitude.toString()

                            latLonCallback.onLocationReceived(lat, lon)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation(object : LatLonCallBack {
                    override fun onLocationReceived(latitude: String, longitude: String) {
                        Log.d("chkGainLocation", "$latitude $longitude 2")
                    }
                })
            }
        }
    }
}