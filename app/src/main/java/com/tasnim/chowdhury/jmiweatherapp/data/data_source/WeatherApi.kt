package com.tasnim.chowdhury.jmiweatherapp.data.data_source

import com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto.WeatherResponse
import com.tasnim.chowdhury.jmiweatherapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("find?")
    suspend fun getWeatherData(
        @Query("lat") lat: String? = Constants.lat,
        @Query("lon") lon: String? = Constants.lon,
        @Query("cnt") cnt: String? = Constants.cnt,
        @Query("appid") appid: String? = Constants.API_KEY,
        @Query("units") units: String? = Constants.unit,
    ) : WeatherResponse

}