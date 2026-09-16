package com.example.feature.create

import com.example.domain.model.Idea

sealed interface CreateProjectAction {
    data class IdeaChanged(val value: String) : CreateProjectAction
    data class TagChanged(val value: String) : CreateProjectAction
    data class LanguageChanged(val value: String) : CreateProjectAction
    data class TemplateChanged(val value: String) : CreateProjectAction
    data class DurationChanged(val value: Int) : CreateProjectAction
    data class AspectRatioChanged(val value: String) : CreateProjectAction
    data object Submit : CreateProjectAction
    data object SaveCurrentIdea : CreateProjectAction
    data class SelectSavedIdea(val idea: Idea) : CreateProjectAction
    data class OpenIdeaDetails(val idea: Idea) : CreateProjectAction
    data object CloseIdeaDetails : CreateProjectAction
    data class ToggleFavorite(val ideaId: String, val isFavorite: Boolean) : CreateProjectAction
    data class UpdateIdeaTag(val ideaId: String, val newTag: String) : CreateProjectAction
    data class RequestDeleteIdea(val idea: Idea) : CreateProjectAction
    data object ConfirmDeleteIdea : CreateProjectAction
    data object DismissDeleteDialog : CreateProjectAction
    data class SetFavoritesFilter(val onlyFavorites: Boolean) : CreateProjectAction
    data class SetTagFilter(val tag: String) : CreateProjectAction
    data object ExportBackupJson : CreateProjectAction
    data object ClearExportedJson : CreateProjectAction
    data object ErrorDismissed : CreateProjectAction
    data object ClearSnackbarMessage : CreateProjectAction
}
