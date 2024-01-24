package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : LifecycleService() {

    var serviceKilled = false
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = baseNotificationBuilder
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        startForegroundService()
    }

    private fun startForegroundService() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}