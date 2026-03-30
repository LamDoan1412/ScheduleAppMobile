package com.example.nhcnhlchtrnh.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val timeInMillis: Long,
    val isDone: Boolean = false,
    val alarmId: Int, // ID riêng biệt cho PendingIntent
    val category: ReminderCategory
)