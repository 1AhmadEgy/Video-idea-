package com.example.data.repository

import com.example.core.database.dao.SceneDao
import com.example.core.database.entity.SceneEntity
import com.example.data.mapper.toDomain
import com.example.domain.model.Scene
import com.example.domain.model.SceneStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao
) : SceneRepository {

    override fun observeScenes(projectId: String): Flow<List<Scene>> {
        return sceneDao.observeScenes(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeScene(sceneId: String): Flow<Scene?> {
        return sceneDao.observeScene(sceneId).map { it?.toDomain() }
    }

    override suspend fun createScene(
        projectId: String,
        narration: String,
        visualPrompt: String,
        position: Int,
        durationSeconds: Int
    ): Scene {
        val entity = SceneEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            position = position,
            narration = narration,
            visualPrompt = visualPrompt,
            durationSeconds = durationSeconds,
            status = SceneStatus.DRAFT.name
        )
        sceneDao.upsert(entity)
        return entity.toDomain()
    }

    override suspend fun updateSceneContent(
        sceneId: String,
        narration: String,
        visualPrompt: String,
        durationSeconds: Int
    ) {
        sceneDao.updateContent(sceneId, narration, visualPrompt, durationSeconds)
    }

    override suspend fun deleteScene(sceneId: String) {
        sceneDao.deleteById(sceneId)
    }
}
