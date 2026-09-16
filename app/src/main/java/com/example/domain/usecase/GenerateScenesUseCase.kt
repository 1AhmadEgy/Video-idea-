package com.example.domain.usecase

import com.example.core.network.AiApi
import com.example.core.network.GenerateScenesRequest
import com.example.data.repository.SceneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateScenesUseCase @Inject constructor(
    private val aiApi: AiApi,
    private val sceneRepository: SceneRepository
) {
    suspend operator fun invoke(
        projectId: String,
        idea: String,
        language: String,
        template: String,
        aspectRatio: String,
        durationSeconds: Int,
        sceneCount: Int
    ) = withContext(Dispatchers.IO) {
        
        val request = GenerateScenesRequest(
            projectId = projectId,
            idea = idea,
            language = language,
            template = template,
            aspectRatio = aspectRatio,
            durationSeconds = durationSeconds,
            sceneCount = sceneCount
        )

        val response = aiApi.generateScenes(projectId, request)

        response.scenes.forEach { dto ->
            sceneRepository.createScene(
                projectId = projectId,
                narration = dto.narration,
                visualPrompt = dto.visualPrompt,
                position = dto.position,
                durationSeconds = dto.durationSeconds
            )
        }
    }
}
