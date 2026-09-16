package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.database.entity.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("""
        SELECT * FROM scenes
        WHERE project_id = :projectId
        ORDER BY position ASC
    """)
    fun observeScenes(projectId: String): Flow<List<SceneEntity>>

    @Query("""
        SELECT * FROM scenes
        WHERE project_id = :projectId
        ORDER BY position ASC
    """)
    suspend fun getScenes(projectId: String): List<SceneEntity>

    @Query("""
        SELECT * FROM scenes
        WHERE id = :sceneId
        LIMIT 1
    """)
    fun observeScene(sceneId: String): Flow<SceneEntity?>

    @Upsert
    suspend fun upsert(scene: SceneEntity)

    @Upsert
    suspend fun upsertAll(scenes: List<SceneEntity>)

    @Query("""
        UPDATE scenes
        SET narration = :narration,
            visual_prompt = :visualPrompt,
            duration_seconds = :durationSeconds
        WHERE id = :sceneId
    """)
    suspend fun updateContent(
        sceneId: String,
        narration: String,
        visualPrompt: String,
        durationSeconds: Int
    )

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun deleteById(sceneId: String)

    @Query("DELETE FROM scenes WHERE project_id = :projectId")
    suspend fun deleteByProjectId(projectId: String)
}
