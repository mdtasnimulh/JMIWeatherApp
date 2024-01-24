package com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Rain(
    @SerializedName("1h")
    val h: Double
)