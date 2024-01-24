package com.tasnim.chowdhury.jmiweatherapp.domain.model

data class CurrentWeatherModel(
    val id: Int,
    val temp: Double,
    val condition: String,
    val icon: String
)
