package com.example.feature.render

data class RenderUiState(
    val isRunning: Boolean = false,
    val progress: Int = 0,
    val stage: String? = null,
    val outputVideoUrl: String? = null,
    val errorMessage: String? = null
)
