package com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Sys(
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("type")
    val type: Int
)