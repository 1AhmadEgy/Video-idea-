package com.example.feature.storyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StoryboardRoute(
    onBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    viewModel: StoryboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StoryboardScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onNavigateToEditor = {
            state.project?.id?.let { projectId ->
                onNavigateToEditor(projectId)
            }
        }
    )
}
