package com.example.data.repository

import com.example.core.database.dao.AssetDao
import com.example.core.database.dao.JobDao
import com.example.core.database.dao.ProjectDao
import com.example.core.database.dao.SceneDao
import javax.inject.Inject
import androidx.room.Transaction

interface ProjectCleanupRepository {
    suspend fun deleteProjectCompletely(projectId: String)
}

class ProjectCleanupRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao,
    private val assetDao: AssetDao,
    private val jobDao: JobDao
) : ProjectCleanupRepository {

    @Transaction
    override suspend fun deleteProjectCompletely(projectId: String) {
        jobDao.deleteByProjectId(projectId)
        assetDao.deleteByProjectId(projectId)
        sceneDao.deleteByProjectId(projectId)
        projectDao.deleteById(projectId)
    }
}
