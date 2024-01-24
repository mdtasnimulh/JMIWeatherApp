package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.util.Constants

const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val lat = intent.getStringExtra("lat") ?: ""
            val lon = intent.getStringExtra("lon") ?: ""

            // Start a Service to fetch weather data and show notification
            val serviceIntent = Intent(context, NotificationService::class.java)
            serviceIntent.putExtra("lat", lat)
            serviceIntent.putExtra("lon", lon)
            context.startService(serviceIntent)
        }
    }
}