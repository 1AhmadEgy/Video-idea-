package com.example.feature.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Project
import com.example.feature.projects.components.DeleteProjectDialog
import com.example.feature.projects.components.EditProjectDialog
import com.example.feature.projects.components.EmptyProjectsView
import com.example.feature.projects.components.ErrorProjectsView
import com.example.feature.projects.components.ProjectCard
import com.example.feature.projects.components.ProjectsFilterBar
import com.example.feature.projects.components.ProjectsSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    state: ProjectsUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (ProjectsAction) -> Unit,
    onCreateProject: () -> Unit,
    onOpenProject: (String) -> Unit
) {
    var projectPendingDelete by remember { mutableStateOf<Project?>(null) }
    var projectPendingEdit by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مشاريعي",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (state.totalCount > 0) {
                            Text(
                                text = "إجمالي المشاريع: ${state.totalCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateProject,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("مشروع جديد") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            ProjectsSearchBar(
                query = state.searchQuery,
                onQueryChange = { onAction(ProjectsAction.SetSearchQuery(it)) }
            )

            // Filters and Sort Row
            ProjectsFilterBar(
                selectedStatus = state.selectedStatus,
                sortType = state.sortType,
                onStatusSelect = { onAction(ProjectsAction.SetStatusFilter(it)) },
                onSortSelect = { onAction(ProjectsAction.SetSortType(it)) },
                onClearFilters = { onAction(ProjectsAction.ClearFilters) }
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    ErrorProjectsView(message = state.error)
                }
                state.projects.isEmpty() -> {
                    val isFiltered = state.searchQuery.isNotBlank() || state.selectedStatus != null
                    EmptyProjectsView(
                        isFiltered = isFiltered,
                        onClearFilters = { onAction(ProjectsAction.ClearFilters) },
                        onCreateProject = onCreateProject
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.projects,
                            key = { it.id }
                        ) { project ->
                            ProjectCard(
                                project = project,
                                onClick = { onOpenProject(project.id) },
                                onEdit = { projectPendingEdit = project },
                                onDuplicate = { onAction(ProjectsAction.DuplicateProject(project.id)) },
                                onDelete = { projectPendingDelete = project }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    projectPendingDelete?.let { project ->
        DeleteProjectDialog(
            project = project,
            onConfirm = {
                projectPendingDelete = null
                onAction(ProjectsAction.DeleteProject(project))
            },
            onDismiss = { projectPendingDelete = null }
        )
    }

    projectPendingEdit?.let { project ->
        EditProjectDialog(
            project = project,
            onConfirm = { newTitle, newIdea ->
                projectPendingEdit = null
                onAction(
                    ProjectsAction.UpdateProject(
                        project.copy(
                            title = newTitle,
                            idea = newIdea
                        )
                    )
                )
            },
            onDismiss = { projectPendingEdit = null }
        )
    }
}
