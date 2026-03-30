package com.example.nhcnhlchtrnh.ui
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.nhcnhlchtrnh.R
import com.example.nhcnhlchtrnh.data.ReminderCategory
import com.example.nhcnhlchtrnh.data.ReminderEntity
import java.text.SimpleDateFormat
import java.util.*

class AddReminderBottomSheet : BottomSheetDialogFragment() {

    // ViewModelFactory cần thiết nếu ViewModel của bạn có tham số
    class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReminderViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private lateinit var viewModel: ReminderViewModel

    // Thuộc tính để nhận thời gian đã chọn từ Activity
    var selectedTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo ViewModel BẰNG CÁCH SỬ DỤNG FACTORY
        // requireActivity().application trả về Application context
        val factory = ReminderViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory).get(ReminderViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.et_title_bottom_sheet)
        val tvSelectedTime = view.findViewById<TextView>(R.id.tv_selected_time)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinner_category)
        val btnSave = view.findViewById<Button>(R.id.btn_save_reminder)

        // 1. Hiển thị thời gian đã chọn
        val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())
        tvSelectedTime.text = "Thời gian đã chọn: " + sdf.format(Date(selectedTimeInMillis))

        // 2. Thiết lập Spinner cho Category
        val categories = ReminderCategory.values().map { it.title }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // 3. Xử lý nút Lưu
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val selectedCategory = ReminderCategory.values()[spinnerCategory.selectedItemPosition]

            if (title.isNotEmpty() && selectedTimeInMillis > System.currentTimeMillis()) {
                val newReminder = ReminderEntity(
                    title = title,
                    timeInMillis = selectedTimeInMillis,
                    alarmId = System.currentTimeMillis().toInt(),
                    category = selectedCategory
                )
                viewModel.insert(newReminder)
                Toast.makeText(requireContext(), "Đã lưu lịch trình ${selectedCategory.title}!", Toast.LENGTH_SHORT).show()
                dismiss() // Đóng Bottom Sheet
            } else if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Thời gian đã chọn đã trôi qua.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}