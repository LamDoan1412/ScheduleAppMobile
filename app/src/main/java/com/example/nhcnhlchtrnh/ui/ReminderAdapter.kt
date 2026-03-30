package com.example.nhcnhlchtrnh.ui // Thay thế bằng package của bạn

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nhcnhlchtrnh.R
import com.example.nhcnhlchtrnh.data.ReminderEntity
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.R as MaterialR

class ReminderAdapter(
    private var reminders: List<ReminderEntity>,
    private val onItemClick: (ReminderEntity) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    fun updateList(newReminders: List<ReminderEntity>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.bind(reminder)
        holder.itemView.setOnClickListener { onItemClick(reminder) }
    }

    override fun getItemCount(): Int = reminders.size

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_reminder_title)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_reminder_time)
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val ivStatusIcon: ImageView = itemView.findViewById(R.id.iv_status_icon)

        fun bind(reminder: ReminderEntity) {
            tvTitle.text = reminder.title

            val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())
            val timeString = sdf.format(Date(reminder.timeInMillis))
            tvTime.text = "Thời gian: $timeString"

            // 1. Cập nhật Icon Danh mục (SỬ DỤNG MÀU NỘI BỘ)
            ivCategoryIcon.setImageResource(reminder.category.iconResId)
            ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.icon_active_color))

            // 2. Cập nhật Trạng thái (Hoàn thành/Đang chờ)
            if (reminder.isDone) {
                // Đã hoàn thành (Màu xám)
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                ivStatusIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                itemView.alpha = 0.7f
            } else {
                // Đang chờ (Màu đỏ/active)
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                ivStatusIcon.setImageResource(R.drawable.ic_bell)
                ivStatusIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.icon_urgent_color))
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                itemView.alpha = 1.0f
            }
        }
    }
}