package com.example.data.repository

import com.example.core.network.AiApi
import com.example.core.network.GenerateScenesRequest
import com.example.core.network.GeneratedScenesResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val api: AiApi
) {
    suspend fun generateScript(
        projectId: String,
        idea: String,
        language: String = "ar",
        template: String = "dynamic",
        aspectRatio: String = "9:16",
        durationSeconds: Int = 30,
        sceneCount: Int = 6
    ): GeneratedScenesResponseDto {
        return api.generateScenes(
            projectId = projectId,
            request = GenerateScenesRequest(
                projectId = projectId,
                idea = idea,
                language = language,
                template = template,
                aspectRatio = aspectRatio,
                durationSeconds = durationSeconds,
                sceneCount = sceneCount
            )
        )
    }
}
