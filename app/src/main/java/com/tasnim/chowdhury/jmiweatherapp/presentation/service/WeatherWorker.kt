package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.data.repository.WeatherRepository
import com.tasnim.chowdhury.jmiweatherapp.util.Constants
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentStatus
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    val currentWeatherState = MutableStateFlow(
        CurrentViewState(
            CurrentStatus.LOADING,
            CurrentDTO(),
            ""
        )
    )

    override suspend fun doWork(): Result {
        val data : Data = inputData
        val lat: String? = data.getString("lat")
        val lon: String? = data.getString("lon")
        fetchCurrentWeatherList(lat, lon)
        Log.d("chkValue", "$lat $lon")
        return Result.success()
    }

    private suspend fun fetchCurrentWeatherList(lat: String?, lon: String?) {
        if (lat != null && lon != null) {
            val result = repository.fetchCurrentWeatherData(lat, lon)
                .catch {
                    // Handle errors here
                }
                .firstOrNull()

            result?.data?.let { currentDTO ->
                showNotification(lat, lon, currentDTO)
            }
        }
    }

    private fun showNotification(lat: String, lon: String, currentDTO: CurrentDTO) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationContent =
            "Lat: $lat, Lon: $lon\nCity: ${currentDTO.name}\nTemperature: ${currentDTO.main?.temp}°C"

        val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Weather Details")
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.scater_clouds)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

}