package com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WeatherResponse(
    @SerializedName("cod")
    val cod: String? = null,
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("list")
    val list: List<Response>? = null,
    @SerializedName("message")
    val message: String? = null
)