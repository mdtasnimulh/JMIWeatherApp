package com.tasnim.chowdhury.jmiweatherapp.data.data_source.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Sys(
    @SerializedName("country")
    val country: String
)