package com.example.core.network

import kotlinx.serialization.Serializable

@Serializable
data class GenerateImagesRequest(
    val sceneIds: List<String>,
    val imageProvider: String = "auto",
    val imageStyle: String = "cinematic"
)

@Serializable
data class GenerateAudioRequest(
    val voice: String = "ar-female-1",
    val language: String = "ar"
)

@Serializable
data class StartRenderRequest(
    val aspectRatio: String = "9:16",
    val resolution: String = "1080x1920",
    val format: String = "mp4",
    val fps: Int = 30
)

@Serializable
data class JobResponse(
    val jobId: String,
    val projectId: String,
    val type: String,
    val status: String,
    val progress: Int = 0,
    val stage: String? = null,
    val message: String? = null,
    val outputUrl: String? = null,
    val error: String? = null
)
