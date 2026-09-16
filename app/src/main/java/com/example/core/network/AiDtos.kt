package com.example.core.network

import kotlinx.serialization.Serializable

@Serializable
data class GenerateScenesRequest(
    val projectId: String,
    val idea: String,
    val language: String,
    val template: String,
    val aspectRatio: String,
    val durationSeconds: Int,
    val sceneCount: Int
)

@Serializable
data class GeneratedScenesResponseDto(
    val projectId: String? = null,
    val provider: String? = null,
    val model: String? = null,
    val scenes: List<GeneratedSceneDto>
)

@Serializable
data class GeneratedSceneDto(
    val position: Int,
    val title: String,
    val narration: String,
    val visualPrompt: String,
    val durationSeconds: Int
)
