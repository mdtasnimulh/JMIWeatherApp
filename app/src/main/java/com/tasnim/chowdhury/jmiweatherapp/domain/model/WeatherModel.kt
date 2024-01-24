package com.tasnim.chowdhury.jmiweatherapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherModel(
    val id: Int,
    val feelsLike: Double,
    val temp: Double,
    val condition: String,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val humidity: Int,
    val windSpeed: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val icon: String
) : Parcelable
