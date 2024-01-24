package com.tasnim.chowdhury.jmiweatherapp.data.data_source.currentDTO

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Clouds(
    @SerializedName("all")
    val all: Int
)