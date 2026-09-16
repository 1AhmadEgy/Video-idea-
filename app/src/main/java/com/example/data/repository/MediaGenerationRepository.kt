package com.example.data.repository

interface MediaGenerationRepository {
    suspend fun requestImageGeneration(projectId: String, sceneIds: List<String>): JobResult
    suspend fun requestAudioGeneration(projectId: String): JobResult
    suspend fun requestFinalRender(projectId: String): JobResult
    suspend fun waitForJob(jobId: String, onProgress: suspend (JobProgress) -> Unit): JobResult
}

data class JobResult(
    val jobId: String,
    val projectId: String,
    val type: String,
    val status: JobStatus,
    val progress: Int,
    val stage: String?,
    val message: String?,
    val outputUrl: String?,
    val error: String?
)

data class JobProgress(
    val jobId: String,
    val status: JobStatus,
    val progress: Int,
    val stage: String?,
    val message: String?
)

enum class JobStatus {
    QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
}
