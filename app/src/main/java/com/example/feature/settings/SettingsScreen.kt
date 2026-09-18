package com.example.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.core.diagnostics.DiagnosticsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val diagnostics = remember { DiagnosticsHelper.getAppDiagnostics(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات والمعلومات") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("إعدادات الذكاء الاصطناعي", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = "https://generativelanguage.googleapis.com/",
                        onValueChange = {},
                        label = { Text("عنوان الخادم (Backend URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "يعمل التطبيق عبر بيئة Google AI Gemini مع دعم خطط التوسعة للنماذج المحلية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("بيانات النسخة الأساسية المحلية", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("المرجع الأساسي: 288476522157701696", style = MaterialTheme.typography.bodyMedium)
                    Text("الإصدار: ${diagnostics.versionName} (${diagnostics.versionCode})", style = MaterialTheme.typography.bodyMedium)
                    Text("المعمارية: Kotlin MVVM + Jetpack Compose M3 + Room + WorkManager", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("معلومات النظام والجهاز", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("الجهاز: ${diagnostics.device}", style = MaterialTheme.typography.bodyMedium)
                    Text("إصدار Android SDK: ${diagnostics.sdk}", style = MaterialTheme.typography.bodyMedium)
                    Text("حالة الأمان: الاتصال المشفر إلزامي (Cleartext معطل)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
