package com.example.nhcnhlchtrnh.data

import com.example.nhcnhlchtrnh.R

enum class ReminderCategory(val title: String, val iconResId: Int) {
    WORK("Công việc", R.drawable.ic_work_24),
    PERSONAL("Cá nhân", R.drawable.ic_person_24),
    HEALTH("Sức khỏe", R.drawable.ic_health_24),
    OTHER("Khác", R.drawable.ic_other_24)
}