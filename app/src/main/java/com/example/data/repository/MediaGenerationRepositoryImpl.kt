package com.example.data.repository

import com.example.core.network.GenerateAudioRequest
import com.example.core.network.GenerateImagesRequest
import com.example.core.network.RenderApi
import com.example.core.network.StartRenderRequest
import com.example.data.mapper.toDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class MediaGenerationRepositoryImpl @Inject constructor(
    private val renderApi: RenderApi
) : MediaGenerationRepository {
    override suspend fun requestImageGeneration(projectId: String, sceneIds: List<String>): JobResult {
        return renderApi.generateImages(
            projectId = projectId,
            request = GenerateImagesRequest(
                sceneIds = sceneIds,
                imageProvider = "auto",
                imageStyle = "cinematic"
            )
        ).toDomain()
    }

    override suspend fun requestAudioGeneration(projectId: String): JobResult {
        return renderApi.generateAudio(
            projectId = projectId,
            request = GenerateAudioRequest(
                voice = "ar-female-1",
                language = "ar"
            )
        ).toDomain()
    }

    override suspend fun requestFinalRender(projectId: String): JobResult {
        return renderApi.startRender(
            projectId = projectId,
            request = StartRenderRequest(
                aspectRatio = "9:16",
                resolution = "1080x1920",
                format = "mp4",
                fps = 30
            )
        ).toDomain()
    }

    override suspend fun waitForJob(jobId: String, onProgress: suspend (JobProgress) -> Unit): JobResult {
        return withTimeout(30 * 60 * 1000L) {
            while (true) {
                val result = renderApi.getJob(jobId).toDomain()
                onProgress(
                    JobProgress(
                        jobId = result.jobId,
                        status = result.status,
                        progress = result.progress,
                        stage = result.stage,
                        message = result.message
                    )
                )

                if (result.status == JobStatus.COMPLETED || result.status == JobStatus.FAILED || result.status == JobStatus.CANCELLED) {
                    return@withTimeout result
                }
                delay(2000)
            }
            throw IllegalStateException("Should not reach here")
        }
    }
}
