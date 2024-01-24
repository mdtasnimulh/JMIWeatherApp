package com.tasnim.chowdhury.jmiweatherapp.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.presentation.MainActivity
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.tasnim.chowdhury.jmiweatherapp.util.Constants.Companion.NOTIFICATION_ID

class NotificationService : Service() {

    lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var runnable: Runnable
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var myBinder = MyBinder()


    override fun onBind(p0: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): NotificationService {
            return this@NotificationService
        }
    }

    fun showNotification() {
        val intent = Intent(baseContext, MainActivity::class.java)
        val mainIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val image = BitmapFactory.decodeResource(baseContext.resources, R.drawable.clear_sky_day)
        val notificationBuilder = notificationBuilder
            .setContentTitle("Weather App")
            .setContentText("Current Weather")
            .setLargeIcon(image)
            .setContentIntent(mainIntent)

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

}
