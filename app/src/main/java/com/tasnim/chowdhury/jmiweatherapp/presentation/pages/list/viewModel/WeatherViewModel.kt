package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto.WeatherResponse
import com.tasnim.chowdhury.jmiweatherapp.data.repository.WeatherRepository
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentStatus
import com.tasnim.chowdhury.jmiweatherapp.util.CurrentViewState
import com.tasnim.chowdhury.jmiweatherapp.util.Status
import com.tasnim.chowdhury.jmiweatherapp.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val currentWeatherState = MutableStateFlow(
        CurrentViewState(
            CurrentStatus.LOADING,
            WeatherResponse(),
            ""
        )
    )

    init {
        fetchWeatherList()
        fetchCurrentWeatherList("","")
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

    fun fetchCurrentWeatherList(lat: String?, lon: String?) {
        currentWeatherState.value = CurrentViewState.loading()
        viewModelScope.launch {
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

}