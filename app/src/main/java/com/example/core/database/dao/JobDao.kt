package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.database.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("""
        SELECT * FROM jobs
        WHERE project_id = :projectId
        ORDER BY created_at DESC
    """)
    fun observeProjectJobs(projectId: String): Flow<List<JobEntity>>

    @Query("""
        SELECT * FROM jobs
        WHERE id = :jobId
        LIMIT 1
    """)
    fun observeJob(jobId: String): Flow<JobEntity?>

    @Upsert
    suspend fun upsert(job: JobEntity)

    @Query("""
        UPDATE jobs
        SET status = :status,
            progress = :progress,
            error_message = :errorMessage,
            updated_at = :updatedAt
        WHERE id = :jobId
    """)
    suspend fun updateProgress(
        jobId: String,
        status: String,
        progress: Int,
        errorMessage: String?,
        updatedAt: Long
    )

    @Query("""
        SELECT * FROM jobs
        WHERE status IN ('QUEUED', 'RUNNING', 'RETRYING')
    """)
    suspend fun getActiveJobs(): List<JobEntity>

    @Query("DELETE FROM jobs WHERE project_id = :projectId")
    suspend fun deleteByProjectId(projectId: String)
}
