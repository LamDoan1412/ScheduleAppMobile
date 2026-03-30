package com.example.nhcnhlchtrnh.utils // Thay thế bằng package của bạn

import android.content.Context
import com.example.nhcnhlchtrnh.data.ReminderEntity

interface AlarmScheduler {
    fun schedule(context: Context, reminder: ReminderEntity)
    fun cancel(context: Context, alarmId: Int)
}