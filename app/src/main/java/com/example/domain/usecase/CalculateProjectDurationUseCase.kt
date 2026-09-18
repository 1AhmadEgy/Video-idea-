package com.example.domain.usecase

import com.example.domain.model.Scene
import javax.inject.Inject

class CalculateProjectDurationUseCase @Inject constructor() {

    operator fun invoke(scenes: List<Scene>): Int {
        return scenes.sumOf { scene ->
            scene.durationSeconds.coerceAtLeast(0)
        }
    }
}
