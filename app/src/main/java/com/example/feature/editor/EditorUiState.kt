package com.example.feature.editor

import com.example.core.database.entity.ProjectEntity
import com.example.core.database.entity.SceneEntity

data class EditorUiState(
    val isLoading: Boolean = true,
    val project: ProjectEntity? = null,
    val scenes: List<SceneEntity> = emptyList(),
    val selectedVoice: String = "ar-female-1",
    val selectedImageStyle: String = "cinematic",
    val error: String? = null,
    val isGenerating: Boolean = false
)
