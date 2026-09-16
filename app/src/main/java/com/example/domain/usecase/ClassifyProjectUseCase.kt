package com.example.domain.usecase

import com.example.domain.model.Scene
import javax.inject.Inject

class ClassifyProjectUseCase @Inject constructor() {

    operator fun invoke(scenes: List<Scene>): String {
        if (scenes.isEmpty()) {
            return "draft"
        }

        val totalDuration = scenes.sumOf {
            it.durationSeconds.coerceAtLeast(0)
        }

        val completedScenes = scenes.count {
            it.narration.isNotBlank() || it.visualPrompt.isNotBlank()
        }

        return when {
            completedScenes == 0 -> "draft"
            completedScenes < scenes.size -> "active"
            totalDuration > 0 -> "completed"
            else -> "active"
        }
    }
}
