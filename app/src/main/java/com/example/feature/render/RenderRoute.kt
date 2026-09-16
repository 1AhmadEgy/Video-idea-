package com.example.feature.render

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RenderRoute(
    projectId: String,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: RenderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(projectId) {
        viewModel.observeRender(projectId)
    }

    RenderScreen(
        projectId = projectId,
        state = state,
        onStartRender = { viewModel.startRender(projectId) },
        onCancelRender = { viewModel.cancelRender(projectId) },
        onShare = {
            state.outputVideoUrl?.let { url ->
                shareVideoUrl(context = context, url = url)
            }
        },
        onDownload = {
            viewModel.saveVideo() // Using MediaStoreSaver for better modern compatibility
        },
        onRenderAgain = { viewModel.startRender(projectId) },
        onBack = onBack
    )
}

private fun shareVideoUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة الفيديو"))
}
