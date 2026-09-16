package com.example.core.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AiApi {
    @POST("api/v1/projects/{projectId}/generate-scenes")
    suspend fun generateScenes(
        @Path("projectId") projectId: String,
        @Body request: GenerateScenesRequest
    ): GeneratedScenesResponseDto
}
