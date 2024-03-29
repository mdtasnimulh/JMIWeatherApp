package com.tasnim.chowdhury.jmiweatherapp.data.repository

import android.util.Log
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.WeatherApi
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto.WeatherResponse
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentViewState
import com.tasnim.chowdhury.jmiweatherapp.util.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherService: WeatherApi) {

    suspend fun fetchWeatherData(): Flow<ViewState<WeatherResponse>> {
        return flow {
            val comment = weatherService.getWeatherData()
            Log.d("chkRepository", "$comment")
            emit(ViewState.success(comment))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchCurrentWeatherData(lat: String, lon: String): Flow<CurrentViewState<CurrentDTO>> {
        return flow {
            val comment = weatherService.getCurrentWeatherData(lat, lon)
            Log.d("chkCurrentWeather", "$comment")
            emit(CurrentViewState.success(comment))
        }.flowOn(Dispatchers.IO)
    }

}