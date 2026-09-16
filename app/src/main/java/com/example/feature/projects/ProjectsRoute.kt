package com.example.feature.projects

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProjectsRoute(
    onCreateProject: () -> Unit,
    onOpenProject: (String) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProjectsEvent.ProjectDeleted -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "تم حذف المشروع بنجاح",
                        actionLabel = "تراجع",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onAction(
                            ProjectsAction.RestoreProject(
                                project = event.project,
                                scenes = event.scenes
                            )
                        )
                    }
                }
                is ProjectsEvent.ProjectDuplicated -> {
                    snackbarHostState.showSnackbar(
                        message = "تم تكرار المشروع بنجاح",
                        duration = SnackbarDuration.Short
                    )
                }
                is ProjectsEvent.ProjectUpdated -> {
                    snackbarHostState.showSnackbar(
                        message = "تم تحديث بيانات المشروع",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    ProjectsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        onCreateProject = onCreateProject,
        onOpenProject = onOpenProject
    )
}
