package com.tasnim.chowdhury.jmiweatherapp

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.data.repository.WeatherRepository
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentStatus
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

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

    suspend fun fetchCurrentWeatherList(lat: String?, lon: String?) {
        currentWeatherState.value = CurrentViewState.loading()
        if (lat != null) {
            if (lon != null) {
                repository.fetchCurrentWeatherData(lat, lon)
                    .catch {
                        currentWeatherState.value = CurrentViewState.error(it.message.toString())
                    }
                    .collect{
                        currentWeatherState.value = CurrentViewState.success(it.data)
                        Log.d("chkCurrentData", "${it.data}")
                    }
            }
        }
    }

}