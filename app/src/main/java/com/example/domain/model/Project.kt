package com.example.domain.model

data class Project(
    val id: String,
    val title: String,
    val idea: String,
    val language: String,
    val template: String,
    val aspectRatio: String,
    val durationSeconds: Int,
    val status: ProjectStatus,
    val createdAt: Long,
    val updatedAt: Long
)

enum class ProjectStatus {
    DRAFT,
    SCRIPT_GENERATING,
    SCRIPT_READY,
    READY_TO_RENDER,
    RENDERING,
    COMPLETED,
    FAILED
}
