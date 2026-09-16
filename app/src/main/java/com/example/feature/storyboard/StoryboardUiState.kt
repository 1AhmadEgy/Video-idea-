package com.example.feature.storyboard

import com.example.domain.model.Project
import com.example.domain.model.Scene

data class StoryboardUiState(
    val project: Project? = null,
    val scenes: List<Scene> = emptyList(),
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val error: String? = null
)
