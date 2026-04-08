package com.example.ca1exam

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ca1exam.ui.theme.CA1ExamTheme
import java.util.*

class TaskSchedulerApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CA1ExamTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        TaskScheduler()
                    }
                }
            }
        }
    }
}

@Composable
fun TaskScheduler() {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Task Scheduler",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = hours,
                onValueChange = { if (it.length <= 2) hours = it },
                label = { Text("HH") },
                modifier = Modifier.width(70.dp)
            )
            Text(":", fontSize = 24.sp)
            OutlinedTextField(
                value = minutes,
                onValueChange = { if (it.length <= 2) minutes = it },
                label = { Text("MM") },
                modifier = Modifier.width(70.dp)
            )
            Text(":", fontSize = 24.sp)
            OutlinedTextField(
                value = seconds,
                onValueChange = { if (it.length <= 2) seconds = it },
                label = { Text("SS") },
                modifier = Modifier.width(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Schedule Task",
            fontSize = 20.sp,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    val h = hours.toIntOrNull() ?: 0
                    val m = minutes.toIntOrNull() ?: 0
                    val s = seconds.toIntOrNull() ?: 0
                    scheduleTask(context, h, m, s)
                }
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskSchedulerPreview() {
    CA1ExamTheme {
        TaskScheduler()
    }
}

fun scheduleTask(context: Context, h: Int, m: Int, s: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, TaskReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, h)
        set(Calendar.MINUTE, m)
        set(Calendar.SECOND, s)
        set(Calendar.MILLISECOND, 0)
        
        // If the time is in the past, schedule it for tomorrow
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    try {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "Task Scheduled for ${calendar.time}", Toast.LENGTH_LONG).show()
    } catch (e: SecurityException) {
        // Handle cases where SCHEDULE_EXACT_ALARM is not granted (Android 12+)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "Task Scheduled (Inexact) for ${calendar.time}", Toast.LENGTH_LONG).show()
    }
}

class TaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Task Executed!", Toast.LENGTH_LONG).show()
    }
}
