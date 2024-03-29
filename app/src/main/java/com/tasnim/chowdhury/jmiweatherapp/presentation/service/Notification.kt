package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

@AndroidEntryPoint
class Notification : BroadcastReceiver() {

    lateinit var data : Data

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val lat = intent.getStringExtra("lat") ?: ""
            val lon = intent.getStringExtra("lon") ?: ""

            Log.d("chkWorker", "Worked")
            setupWorker(context, lat, lon)
        }
    }

    private fun setupWorker(context: Context, latitude: String, longitude: String) {
        data = Data.Builder().putString("lat", latitude).putString("lon", longitude).build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "weather_notification",
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )
    }
}