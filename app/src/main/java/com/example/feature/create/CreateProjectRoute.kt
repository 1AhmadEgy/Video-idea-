package com.example.feature.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateProjectRoute(
    onProjectCreated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.createdProjectId) {
        state.value.createdProjectId?.let(onProjectCreated)
    }

    CreateProjectScreen(
        state = state.value,
        onAction = viewModel::onAction,
        onBack = onBack
    )
}
