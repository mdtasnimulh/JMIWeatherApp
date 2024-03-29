package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO.CurrentDTO
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto.WeatherResponse
import com.tasnim.chowdhury.jmiweatherapp.data.repository.WeatherRepository
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentViewState
import com.tasnim.chowdhury.jmiweatherapp.util.Status
import com.tasnim.chowdhury.jmiweatherapp.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
): ViewModel() {

    val weatherState = MutableStateFlow(
        ViewState(
            Status.LOADING,
            WeatherResponse(),
            ""
        )
    )

    init {
        fetchWeatherList()
    }

    fun fetchWeatherList() {
        weatherState.value = ViewState.loading()
        viewModelScope.launch {
            repository.fetchWeatherData()
                .catch {
                    weatherState.value = ViewState.error(it.message.toString())
                }
                .collect{
                    weatherState.value = ViewState.success(it.data)
                    Log.d("chkData", "${it.data}")
                }
        }
    }

    suspend fun fetchCurrentWeatherData(lat: String, lon: String): Flow<CurrentViewState<CurrentDTO>> {
        return repository.fetchCurrentWeatherData(lat, lon)
    }

}