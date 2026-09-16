package com.example.domain.model

data class Scene(
    val id: String,
    val projectId: String,
    val position: Int,
    val narration: String,
    val visualPrompt: String,
    val durationSeconds: Int,
    val status: SceneStatus
)

enum class SceneStatus {
    DRAFT,
    READY,
    GENERATING,
    FAILED
}
