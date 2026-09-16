package com.example.domain.usecase

import com.example.core.worker.RenderWorkScheduler
import javax.inject.Inject

class StartFinalRenderUseCase @Inject constructor(
    private val scheduler: RenderWorkScheduler
) {
    operator fun invoke(projectId: String, sceneIds: List<String>) {
        scheduler.startFinalRender(projectId = projectId, sceneIds = sceneIds)
    }
}
