package com.example.feature.render

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderScreen(
    projectId: String,
    state: RenderUiState,
    onStartRender: () -> Unit,
    onCancelRender: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onRenderAgain: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الرندر النهائي") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isRunning -> {
                    LinearProgressIndicator(
                        progress = { state.progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "${state.progress}%")
                    Text(text = state.stage ?: "جارٍ التنفيذ...")
                    CircularProgressIndicator()

                    OutlinedButton(
                        onClick = onCancelRender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "إلغاء")
                    }
                }

                state.outputVideoUrl != null -> {
                    VideoPlayerScreen(
                        videoUrl = state.outputVideoUrl,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = onShare,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "مشاركة الفيديو")
                    }

                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "حفظ الفيديو")
                    }

                    OutlinedButton(
                        onClick = onRenderAgain,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "إعادة الرندر")
                    }
                }

                state.errorMessage != null -> {
                    Text(text = state.errorMessage)

                    Button(
                        onClick = onRenderAgain,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "المحاولة مرة أخرى")
                    }
                }

                else -> {
                    Text(text = "تصدير المشروع: $projectId")
                    
                    Button(
                        onClick = onStartRender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "بدء الرندر النهائي")
                    }
                }
            }
        }
    }
}
