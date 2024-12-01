package com.arfsar.smarttrash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BinCapacityService : Service() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var notificationSent = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        database.child("sensor").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sensorData = snapshot.getValue(SensorModel::class.java)
                val capacity = sensorData?.kapasitas?.toFloatOrNull() ?: 0f
                if (capacity >= 80 && !notificationSent) {
                    showNotification("Tong sampah penuh", "kapasitas sampah: $capacity%")
                    notificationSent = true
                } else if  (capacity < 80) {
                    notificationSent = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BinCapacityService", "Failed to read value.", error.toException())
            }

        })
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "bin_capacity_channel"
            val channelName = "Bin Capacity Notifications"
            val channelDescription = "Notifications for bin capacity status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "bin_capacity_channel"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}