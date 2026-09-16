package com.example.data.repository

import com.example.domain.model.Project
import com.example.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeProjects(): Flow<List<Project>>
    fun observeProject(projectId: String): Flow<Project?>
    suspend fun getProject(projectId: String): Project?
    suspend fun getScenesForProject(projectId: String): List<Scene>
    suspend fun createDraft(
        idea: String,
        language: String,
        template: String,
        aspectRatio: String,
        durationSeconds: Int
    ): Project
    suspend fun updateProject(project: Project)
    suspend fun duplicateProject(projectId: String): Project?
    suspend fun restoreProject(project: Project, scenes: List<Scene>)
    suspend fun deleteProject(projectId: String)
}
