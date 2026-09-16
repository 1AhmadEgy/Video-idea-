package com.example.feature.create

import com.example.domain.model.Idea

data class CreateProjectUiState(
    val idea: String = "",
    val tag: String = "Shorts",
    val language: String = "ar",
    val template: String = "educational",
    val durationSeconds: Int = 30,
    val aspectRatio: String = "9:16",
    val isSubmitting: Boolean = false,
    val createdProjectId: String? = null,
    val error: String? = null,
    val savedIdeas: List<Idea> = emptyList(),
    val filterOnlyFavorites: Boolean = false,
    val selectedFilterTag: String = "الكل",
    val selectedIdeaForDetails: Idea? = null,
    val ideaToDelete: Idea? = null,
    val snackbarMessage: String? = null,
    val exportedJsonContent: String? = null
)
