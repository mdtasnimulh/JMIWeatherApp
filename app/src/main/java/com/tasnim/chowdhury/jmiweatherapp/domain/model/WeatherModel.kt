package com.tasnim.chowdhury.jmiweatherapp.domain.model

data class WeatherModel(
    val id: Int,
    val feelsLike: Double,
    val temp: Double,
    val condition: String,
    val cityName: String
)
