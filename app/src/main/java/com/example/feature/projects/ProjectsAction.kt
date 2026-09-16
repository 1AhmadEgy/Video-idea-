package com.example.feature.projects

import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import com.example.feature.projects.components.ProjectSortType

sealed interface ProjectsAction {
    data object Refresh : ProjectsAction
    data class OpenProject(val projectId: String) : ProjectsAction
    data class DeleteProject(val project: Project) : ProjectsAction
    data class DuplicateProject(val projectId: String) : ProjectsAction
    data class UpdateProject(val project: Project) : ProjectsAction
    data class RestoreProject(val project: Project, val scenes: List<Scene>) : ProjectsAction
    data class SetSearchQuery(val query: String) : ProjectsAction
    data class SetStatusFilter(val status: ProjectStatus?) : ProjectsAction
    data class SetSortType(val sortType: ProjectSortType) : ProjectsAction
    data object ClearFilters : ProjectsAction
}
