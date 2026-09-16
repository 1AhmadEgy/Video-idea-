package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.database.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("""
        SELECT * FROM projects
        ORDER BY updated_at DESC
    """)
    fun observeProjects(): Flow<List<ProjectEntity>>

    @Query("""
        SELECT * FROM projects
        WHERE id = :projectId
        LIMIT 1
    """)
    fun observeProject(projectId: String): Flow<ProjectEntity?>

    @Query("""
        SELECT * FROM projects
        WHERE id = :projectId
        LIMIT 1
    """)
    suspend fun getProject(projectId: String): ProjectEntity?

    @Upsert
    suspend fun upsert(project: ProjectEntity)

    @Upsert
    suspend fun upsertAll(projects: List<ProjectEntity>)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteById(projectId: String)
}
