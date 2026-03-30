package com.example.nhcnhlchtrnh.utils // Thay thế bằng package của bạn

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log // Import Log để gỡ lỗi
import com.example.nhcnhlchtrnh.AlarmReceiver
import com.example.nhcnhlchtrnh.data.ReminderEntity

class AlarmSchedulerImpl : AlarmScheduler {

    override fun schedule(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // ****************************************************
        // BƯỚC KHẮC PHỤC: KIỂM TRA QUYỀN Exact Alarm (API 31/S trở lên)
        // ****************************************************
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmScheduler", "Không có quyền SCHEDULE_EXACT_ALARMS. Báo thức chính xác không được đặt.")
                // Không thể đặt báo thức chính xác, thoát khỏi hàm.
                // Nếu muốn báo thức vẫn chạy nhưng kém chính xác,
                // bạn có thể dùng alarmManager.set() thay vì setExactAndAllowWhileIdle().
                return
            }
        }

        // --- Logic tạo PendingIntent ---
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_REMINDER_ID", reminder.id)
            putExtra("EXTRA_ALARM_ID", reminder.alarmId)
            putExtra("EXTRA_TITLE", reminder.title)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_MUTABLE

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.alarmId, // Sử dụng alarmId làm Request Code
            intent,
            flags
        )

        // Thực hiện đặt báo thức chính xác
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.timeInMillis,
            pendingIntent
        )
    }

    // Phần cancel giữ nguyên vì không liên quan đến quyền Exact Alarm
    override fun cancel(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_MUTABLE

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId, // Phải trùng với Request Code khi đặt
            intent,
            flags
        )

        alarmManager.cancel(pendingIntent)
    }
}