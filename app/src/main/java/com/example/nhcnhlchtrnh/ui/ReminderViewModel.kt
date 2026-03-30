package com.example.nhcnhlchtrnh.ui // Thay thế bằng package của bạn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nhcnhlchtrnh.data.AppDatabase
import com.example.nhcnhlchtrnh.data.ReminderEntity
import com.example.nhcnhlchtrnh.utils.AlarmScheduler
import com.example.nhcnhlchtrnh.utils.AlarmSchedulerImpl
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderDao = AppDatabase.getDatabase(application).reminderDao()
    private val scheduler: AlarmScheduler = AlarmSchedulerImpl()

    val allReminders = reminderDao.getAllReminders().asLiveData()

    fun insert(reminder: ReminderEntity) = viewModelScope.launch {
        // Lưu vào DB (lấy được ID mới)
        val newId = reminderDao.insert(reminder)

        // Cập nhật Entity với ID mới (sử dụng ID này cho cả DB và Alarm)
        val updatedReminder = reminder.copy(id = newId.toInt(), alarmId = newId.toInt())
        reminderDao.update(updatedReminder)

        // Đặt báo thức
        scheduler.schedule(getApplication(), updatedReminder)
    }

    // THÊM: Hàm update tổng quát (để dùng cho đánh dấu hoàn thành)
    fun update(reminder: ReminderEntity) = viewModelScope.launch {
        reminderDao.update(reminder)

        // Nếu mục không hoàn thành và còn ở tương lai, đảm bảo báo thức được đặt
        if (!reminder.isDone && reminder.timeInMillis > System.currentTimeMillis()) {
            scheduler.schedule(getApplication(), reminder)
        }

        // Nếu đánh dấu là HOÀN THÀNH, hủy báo thức
        if (reminder.isDone) {
            scheduler.cancel(getApplication(), reminder.alarmId)
        }
    }

    fun delete(reminder: ReminderEntity) = viewModelScope.launch {
        reminderDao.delete(reminder)
        scheduler.cancel(getApplication(), reminder.alarmId)
    }
}