package com.example.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("إعدادات الذكاء الاصطناعي", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = "https://generativelanguage.googleapis.com/",
                onValueChange = {},
                label = { Text("عنوان الخادم (Backend URL)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Text("ملاحظة: لربط خادم Node.js محلي بـ Ollama، يمكنك تعديل العنوان هنا في المستقبل.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
