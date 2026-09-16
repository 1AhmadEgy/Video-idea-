package com.example.data.mapper

import com.example.core.database.entity.SceneEntity
import com.example.domain.model.Scene
import com.example.domain.model.SceneStatus

fun SceneEntity.toDomain(): Scene {
    return Scene(
        id = id,
        projectId = projectId,
        position = position,
        narration = narration,
        visualPrompt = visualPrompt,
        durationSeconds = durationSeconds,
        status = runCatching {
            SceneStatus.valueOf(status)
        }.getOrDefault(SceneStatus.DRAFT)
    )
}

fun Scene.toEntity(): SceneEntity {
    return SceneEntity(
        id = id,
        projectId = projectId,
        position = position,
        narration = narration,
        visualPrompt = visualPrompt,
        durationSeconds = durationSeconds,
        status = status.name
    )
}
