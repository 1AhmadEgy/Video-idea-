package com.example.feature.projects

import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import com.example.feature.projects.components.ProjectSortType

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: ProjectStatus? = null,
    val sortType: ProjectSortType = ProjectSortType.LAST_UPDATED,
    val error: String? = null,
    val lastDeletedProject: Project? = null,
    val lastDeletedScenes: List<Scene> = emptyList()
)
