package com.example.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ProjectCleanupRepository
import com.example.data.repository.ProjectRepository
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import com.example.feature.projects.components.ProjectSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProjectsEvent {
    data class ProjectDeleted(val project: Project, val scenes: List<Scene>) : ProjectsEvent
    data class ProjectDuplicated(val newProject: Project) : ProjectsEvent
    data class ProjectUpdated(val project: Project) : ProjectsEvent
}

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val cleanupRepository: ProjectCleanupRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<ProjectStatus?>(null)
    private val sortType = MutableStateFlow(ProjectSortType.LAST_UPDATED)

    private val _events = MutableSharedFlow<ProjectsEvent>()
    val events: SharedFlow<ProjectsEvent> = _events.asSharedFlow()

    private var lastDeletedProject: Project? = null
    private var lastDeletedScenes: List<Scene> = emptyList()

    val uiState: StateFlow<ProjectsUiState> =
        combine(
            repository.observeProjects(),
            searchQuery,
            selectedStatus,
            sortType
        ) { rawProjects, query, status, sort ->
            val filtered = rawProjects
                .filter { project ->
                    val matchesQuery = query.isBlank() ||
                            project.title.contains(query, ignoreCase = true) ||
                            project.idea.contains(query, ignoreCase = true)

                    val matchesStatus = status == null || project.status == status

                    matchesQuery && matchesStatus
                }
                .let { list ->
                    when (sort) {
                        ProjectSortType.LAST_UPDATED -> list.sortedByDescending { it.updatedAt }
                        ProjectSortType.CREATED_DATE -> list.sortedByDescending { it.createdAt }
                        ProjectSortType.NAME -> list.sortedBy { it.title.lowercase() }
                    }
                }

            ProjectsUiState(
                projects = filtered,
                totalCount = rawProjects.size,
                isLoading = false,
                searchQuery = query,
                selectedStatus = status,
                sortType = sort,
                lastDeletedProject = lastDeletedProject,
                lastDeletedScenes = lastDeletedScenes
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProjectsUiState()
        )

    fun onAction(action: ProjectsAction) {
        when (action) {
            is ProjectsAction.DeleteProject -> {
                viewModelScope.launch {
                    val project = action.project
                    val scenes = repository.getScenesForProject(project.id)
                    lastDeletedProject = project
                    lastDeletedScenes = scenes
                    cleanupRepository.deleteProjectCompletely(project.id)
                    _events.emit(ProjectsEvent.ProjectDeleted(project, scenes))
                }
            }
            is ProjectsAction.RestoreProject -> {
                viewModelScope.launch {
                    repository.restoreProject(action.project, action.scenes)
                    lastDeletedProject = null
                    lastDeletedScenes = emptyList()
                }
            }
            is ProjectsAction.DuplicateProject -> {
                viewModelScope.launch {
                    val duplicated = repository.duplicateProject(action.projectId)
                    if (duplicated != null) {
                        _events.emit(ProjectsEvent.ProjectDuplicated(duplicated))
                    }
                }
            }
            is ProjectsAction.UpdateProject -> {
                viewModelScope.launch {
                    val updated = action.project.copy(updatedAt = System.currentTimeMillis())
                    repository.updateProject(updated)
                    _events.emit(ProjectsEvent.ProjectUpdated(updated))
                }
            }
            is ProjectsAction.SetSearchQuery -> {
                searchQuery.value = action.query
            }
            is ProjectsAction.SetStatusFilter -> {
                selectedStatus.value = action.status
            }
            is ProjectsAction.SetSortType -> {
                sortType.value = action.sortType
            }
            ProjectsAction.ClearFilters -> {
                searchQuery.value = ""
                selectedStatus.value = null
                sortType.value = ProjectSortType.LAST_UPDATED
            }
            ProjectsAction.Refresh -> {}
            is ProjectsAction.OpenProject -> {}
        }
    }
}
