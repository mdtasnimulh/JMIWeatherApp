package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list.viewModel.WeatherViewModel
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationService(private val weatherViewModel: WeatherViewModel) : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val lat = intent.getStringExtra("lat") ?: ""
            val lon = intent.getStringExtra("lon") ?: ""

            // Fetch weather data and show notification
            GlobalScope.launch(Dispatchers.Main) {
                val result = weatherViewModel.fetchCurrentWeatherData(lat, lon).firstOrNull()
                result?.data?.let { currentDTO ->
                    showNotification(lat, lon, currentDTO)
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun showNotification(lat: String, lon: String, currentDTO: CurrentDTO) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationContent =
            "Lat: $lat, Lon: $lon\nCity: ${currentDTO.name}\nTemperature: ${currentDTO.main?.temp}°C"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Weather Details")
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.scater_clouds)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}