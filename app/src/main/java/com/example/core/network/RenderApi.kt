package com.example.core.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RenderApi {
    @POST("api/v1/projects/{projectId}/generate-images")
    suspend fun generateImages(
        @Path("projectId") projectId: String,
        @Body request: GenerateImagesRequest
    ): JobResponse

    @POST("api/v1/projects/{projectId}/generate-audio")
    suspend fun generateAudio(
        @Path("projectId") projectId: String,
        @Body request: GenerateAudioRequest
    ): JobResponse

    @POST("api/v1/projects/{projectId}/render")
    suspend fun startRender(
        @Path("projectId") projectId: String,
        @Body request: StartRenderRequest
    ): JobResponse

    @GET("api/v1/jobs/{jobId}")
    suspend fun getJob(
        @Path("jobId") jobId: String
    ): JobResponse
}
