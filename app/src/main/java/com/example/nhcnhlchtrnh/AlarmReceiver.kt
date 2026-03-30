package com.example.nhcnhlchtrnh

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "reminder_channel_id"
    private val TAG = "ALARM_DEBUG" // Khai báo một TAG dễ lọc
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive KÍCH HOẠT! Đang chuẩn bị hiển thị thông báo.")
        val alarmId = intent.getIntExtra("EXTRA_ALARM_ID", -1)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Đã đến giờ nhắc nhở!"

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ Lịch Trình Tới!")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(alarmId, notification)
        Log.d(TAG, "Đã gọi manager.notify() cho ID: $alarmId")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Kênh Nhắc Nhở Quan Trọng"
            val descriptionText = "Thông báo cho các lịch trình đã đặt"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}