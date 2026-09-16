package com.example.data.repository

import com.example.core.database.dao.ProjectDao
import com.example.core.database.dao.SceneDao
import com.example.core.database.entity.ProjectEntity
import com.example.core.database.entity.SceneEntity
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao
) : ProjectRepository {

    override fun observeProjects(): Flow<List<Project>> {
        return projectDao.observeProjects()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun observeProject(projectId: String): Flow<Project?> {
        return projectDao.observeProject(projectId)
            .map { entity ->
                entity?.toDomain()
            }
    }

    override suspend fun getProject(projectId: String): Project? {
        return projectDao.getProject(projectId)?.toDomain()
    }

    override suspend fun getScenesForProject(projectId: String): List<Scene> {
        return sceneDao.getScenes(projectId).map { it.toDomain() }
    }

    override suspend fun createDraft(
        idea: String,
        language: String,
        template: String,
        aspectRatio: String,
        durationSeconds: Int
    ): Project {
        val now = System.currentTimeMillis()
        val entity = ProjectEntity(
            id = UUID.randomUUID().toString(),
            title = idea.take(40),
            idea = idea,
            language = language,
            template = template,
            aspectRatio = aspectRatio,
            durationSeconds = durationSeconds,
            status = ProjectStatus.DRAFT.name,
            createdAt = now,
            updatedAt = now
        )
        projectDao.upsert(entity)
        return entity.toDomain()
    }

    override suspend fun updateProject(project: Project) {
        projectDao.upsert(project.toEntity())
    }

    override suspend fun duplicateProject(projectId: String): Project? {
        val sourceProject = projectDao.getProject(projectId) ?: return null
        val now = System.currentTimeMillis()
        val newProjectId = UUID.randomUUID().toString()

        val duplicatedProject = sourceProject.copy(
            id = newProjectId,
            title = "${sourceProject.title} (نسخة)",
            createdAt = now,
            updatedAt = now
        )
        projectDao.upsert(duplicatedProject)

        val sourceScenes = sceneDao.getScenes(projectId)
        if (sourceScenes.isNotEmpty()) {
            val duplicatedScenes = sourceScenes.map { scene ->
                scene.copy(
                    id = UUID.randomUUID().toString(),
                    projectId = newProjectId
                )
            }
            sceneDao.upsertAll(duplicatedScenes)
        }

        return duplicatedProject.toDomain()
    }

    override suspend fun restoreProject(project: Project, scenes: List<Scene>) {
        projectDao.upsert(project.toEntity())
        if (scenes.isNotEmpty()) {
            sceneDao.upsertAll(scenes.map { it.toEntity() })
        }
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteById(projectId)
    }
}
