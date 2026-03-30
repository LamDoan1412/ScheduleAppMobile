package com.example.nhcnhlchtrnh.ui

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import com.example.nhcnhlchtrnh.R
import com.example.nhcnhlchtrnh.data.ReminderCategory
import com.example.nhcnhlchtrnh.data.ReminderEntity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReminderViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Cần quyền thông báo để nhắc nhở!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sử dụng ViewModelProvider mặc định (AndroidViewModelFactory tự động xử lý Application)
        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        setContent {
            MaterialTheme {
                ReminderScreen(
                    viewModel = viewModel,
                    supportFragmentManager = supportFragmentManager
                )
            }
        }

        requestNotificationPermission()
        checkExactAlarmPermission()
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel,
    supportFragmentManager: androidx.fragment.app.FragmentManager
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val reminders by viewModel.allReminders.asFlow().collectAsState(initial = emptyList())

    // State cho Add Reminder BottomSheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTimeForSheet by remember { mutableStateOf(0L) }

    val filteredReminders = remember(reminders, selectedDate) {
        reminders.filter {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.timeInMillis
            cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
        }
    }

    // Hàm hiển thị TimePicker
    fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText("Chọn giờ nhắc nhở")
            // Áp dụng Style bo tròn cho TimePicker
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val finalCalendar = Calendar.getInstance()
            finalCalendar.timeInMillis = selectedDate.timeInMillis
            finalCalendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            finalCalendar.set(Calendar.MINUTE, timePicker.minute)
            finalCalendar.set(Calendar.SECOND, 0)
            
            selectedTimeForSheet = finalCalendar.timeInMillis
            showBottomSheet = true
        }
        timePicker.show(supportFragmentManager, "MATERIAL_TIME_PICKER")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTimePicker() },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm nhắc nhở", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bckg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Lớp phủ mờ nhẹ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Calendar Container
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            CalendarView(ctx).apply {
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    val newDate = Calendar.getInstance()
                                    newDate.set(year, month, dayOfMonth)
                                    selectedDate = newDate
                                }
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            }
                        },
                        update = { view ->
                            view.date = selectedDate.timeInMillis
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                if (filteredReminders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "Không có lịch trình nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredReminders, key = { it.id }) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onClick = { 
                                    showReminderOptionsDialog(context, viewModel, reminder)
                                },
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = tween(durationMillis = 300)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AddReminderContent(
                selectedTimeInMillis = selectedTimeForSheet,
                onSave = { title, category, time ->
                    // KIỂM TRA THỜI GIAN: Nếu thời gian đã chọn nhỏ hơn thời gian hiện tại thì báo lỗi
                    if (time > System.currentTimeMillis()) {
                        val newReminder = ReminderEntity(
                            title = title,
                            timeInMillis = time,
                            alarmId = System.currentTimeMillis().toInt(),
                            category = category
                        )
                        viewModel.insert(newReminder)
                        Toast.makeText(context, "Đã lưu lịch trình!", Toast.LENGTH_SHORT).show()
                        showBottomSheet = false
                    } else {
                        Toast.makeText(context, "Thời gian đã trôi qua, không thể đặt lịch!", Toast.LENGTH_SHORT).show()
                    }
                },
                onCancel = { showBottomSheet = false }
            )
        }
    }
}

@Composable
fun AddReminderContent(
    selectedTimeInMillis: Long,
    onSave: (String, ReminderCategory, Long) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ReminderCategory.WORK) }
    val context = LocalContext.current
    
    val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())
    val timeString = sdf.format(Date(selectedTimeInMillis))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding() // Tránh bị che bởi thanh điều hướng
    ) {
        Text(
            text = "Thêm nhắc nhở mới",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tiêu đề") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Danh mục:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ReminderCategory.values()) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category.title) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = category.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(colorResource(id = R.color.icon_active_color))
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Thời gian: $timeString",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, selectedCategory, selectedTimeInMillis)
                } else {
                    Toast.makeText(context, "Vui lòng nhập tiêu đề!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Lưu Lịch Trình", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun showReminderOptionsDialog(context: Context, viewModel: ReminderViewModel, reminder: ReminderEntity) {
    val items = arrayOf("Xóa lịch trình", "Đánh dấu hoàn thành")
    AlertDialog.Builder(context)
        .setTitle("Tùy chọn")
        .setItems(items) { _, which ->
            when (which) {
                0 -> viewModel.delete(reminder)
                1 -> viewModel.update(reminder.copy(isDone = !reminder.isDone))
            }
        }
        .show()
}

@Composable
fun ReminderItem(
    reminder: ReminderEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .alpha(if (reminder.isDone) 0.7f else 1.0f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = reminder.category.iconResId),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(colorResource(id = R.color.icon_active_color))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reminder.isDone) Color.Gray else Color.Black,
                    textDecoration = if (reminder.isDone) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())
                val timeString = sdf.format(Date(reminder.timeInMillis))
                
                Text(
                    text = "Thời gian: $timeString",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            val statusIcon = if (reminder.isDone) R.drawable.ic_check_circle else R.drawable.ic_bell
            val statusColor = if (reminder.isDone) Color.Gray else colorResource(id = R.color.icon_urgent_color)

            Image(
                painter = painterResource(id = statusIcon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(statusColor)
            )
        }
    }
}