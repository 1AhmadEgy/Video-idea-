package com.example.data.repository

import com.example.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(projectId: String): Flow<List<Scene>>
    fun observeScene(sceneId: String): Flow<Scene?>
    suspend fun createScene(projectId: String, narration: String, visualPrompt: String, position: Int, durationSeconds: Int): Scene
    suspend fun updateSceneContent(sceneId: String, narration: String, visualPrompt: String, durationSeconds: Int)
    suspend fun deleteScene(sceneId: String)
}
