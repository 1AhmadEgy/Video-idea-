package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.database.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("""
        SELECT * FROM assets
        WHERE project_id = :projectId
        ORDER BY created_at DESC
    """)
    fun observeProjectAssets(projectId: String): Flow<List<AssetEntity>>

    @Query("""
        SELECT * FROM assets
        WHERE scene_id = :sceneId
        ORDER BY created_at DESC
    """)
    fun observeSceneAssets(sceneId: String): Flow<List<AssetEntity>>

    @Upsert
    suspend fun upsert(asset: AssetEntity)

    @Query("DELETE FROM assets WHERE id = :assetId")
    suspend fun deleteById(assetId: String)

    @Query("DELETE FROM assets WHERE project_id = :projectId")
    suspend fun deleteByProjectId(projectId: String)

    @Query("SELECT * FROM assets")
    suspend fun getAllAssets(): List<AssetEntity>
}

