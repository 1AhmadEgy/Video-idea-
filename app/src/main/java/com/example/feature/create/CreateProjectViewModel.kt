package com.example.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.IdeaRepository
import com.example.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProjectUiState())

    val uiState: StateFlow<CreateProjectUiState> =
        combine(
            _uiState,
            ideaRepository.observeIdeas()
        ) { state, ideas ->
            state.copy(savedIdeas = ideas)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CreateProjectUiState()
        )

    fun onAction(action: CreateProjectAction) {
        when (action) {
            is CreateProjectAction.IdeaChanged -> {
                _uiState.update { it.copy(idea = action.value, error = null) }
            }
            is CreateProjectAction.TagChanged -> {
                _uiState.update { it.copy(tag = action.value) }
            }
            is CreateProjectAction.LanguageChanged -> {
                _uiState.update { it.copy(language = action.value) }
            }
            is CreateProjectAction.TemplateChanged -> {
                _uiState.update { it.copy(template = action.value) }
            }
            is CreateProjectAction.DurationChanged -> {
                _uiState.update { it.copy(durationSeconds = action.value) }
            }
            is CreateProjectAction.AspectRatioChanged -> {
                _uiState.update { it.copy(aspectRatio = action.value) }
            }
            CreateProjectAction.Submit -> submit()
            CreateProjectAction.SaveCurrentIdea -> saveCurrentIdea()
            is CreateProjectAction.SelectSavedIdea -> {
                _uiState.update {
                    it.copy(
                        idea = action.idea.text,
                        tag = action.idea.tag,
                        error = null,
                        selectedIdeaForDetails = null
                    )
                }
            }
            is CreateProjectAction.OpenIdeaDetails -> {
                _uiState.update { it.copy(selectedIdeaForDetails = action.idea) }
            }
            CreateProjectAction.CloseIdeaDetails -> {
                _uiState.update { it.copy(selectedIdeaForDetails = null) }
            }
            is CreateProjectAction.ToggleFavorite -> {
                viewModelScope.launch {
                    ideaRepository.toggleFavorite(action.ideaId, action.isFavorite)
                    _uiState.update { current ->
                        val updatedDetails = if (current.selectedIdeaForDetails?.id == action.ideaId) {
                            current.selectedIdeaForDetails.copy(isFavorite = action.isFavorite)
                        } else {
                            current.selectedIdeaForDetails
                        }
                        current.copy(selectedIdeaForDetails = updatedDetails)
                    }
                }
            }
            is CreateProjectAction.UpdateIdeaTag -> {
                viewModelScope.launch {
                    ideaRepository.updateTag(action.ideaId, action.newTag)
                    _uiState.update { current ->
                        val updatedDetails = if (current.selectedIdeaForDetails?.id == action.ideaId) {
                            current.selectedIdeaForDetails.copy(tag = action.newTag)
                        } else {
                            current.selectedIdeaForDetails
                        }
                        current.copy(
                            selectedIdeaForDetails = updatedDetails,
                            snackbarMessage = "تم تحديث تصنيف الفكرة"
                        )
                    }
                }
            }
            is CreateProjectAction.RequestDeleteIdea -> {
                _uiState.update {
                    it.copy(
                        ideaToDelete = action.idea,
                        selectedIdeaForDetails = null
                    )
                }
            }
            CreateProjectAction.ConfirmDeleteIdea -> {
                val toDelete = _uiState.value.ideaToDelete
                if (toDelete != null) {
                    viewModelScope.launch {
                        ideaRepository.deleteIdea(toDelete.id)
                        _uiState.update {
                            it.copy(
                                ideaToDelete = null,
                                snackbarMessage = "تم حذف الفكرة بنجاح"
                            )
                        }
                    }
                }
            }
            CreateProjectAction.DismissDeleteDialog -> {
                _uiState.update { it.copy(ideaToDelete = null) }
            }
            is CreateProjectAction.SetFavoritesFilter -> {
                _uiState.update { it.copy(filterOnlyFavorites = action.onlyFavorites) }
            }
            is CreateProjectAction.SetTagFilter -> {
                _uiState.update { it.copy(selectedFilterTag = action.tag) }
            }
            CreateProjectAction.ExportBackupJson -> {
                viewModelScope.launch {
                    val json = ideaRepository.exportIdeasJson()
                    _uiState.update {
                        it.copy(
                            exportedJsonContent = json,
                            snackbarMessage = "تم إنشاء ملف النسخة الاحتياطية بنجاح"
                        )
                    }
                }
            }
            CreateProjectAction.ClearExportedJson -> {
                _uiState.update { it.copy(exportedJsonContent = null) }
            }
            CreateProjectAction.ErrorDismissed -> {
                _uiState.update { it.copy(error = null) }
            }
            CreateProjectAction.ClearSnackbarMessage -> {
                _uiState.update { it.copy(snackbarMessage = null) }
            }
        }
    }

    private fun saveCurrentIdea() {
        val text = _uiState.value.idea.trim()
        val tag = _uiState.value.tag
        if (text.isBlank()) {
            _uiState.update { it.copy(error = "يرجى كتابة فكرة أولاً لحفظها") }
            return
        }

        viewModelScope.launch {
            ideaRepository.saveIdea(text = text, tag = tag)
            _uiState.update {
                it.copy(snackbarMessage = "تم حفظ الفكرة في الأفكار السابقة بنجاح")
            }
        }
    }

    private fun submit() {
        val current = _uiState.value

        if (current.idea.trim().length < 3) {
            _uiState.update { it.copy(error = "اكتب فكرة تحتوي على ثلاثة أحرف على الأقل") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            runCatching {
                // Automatically save idea with tag to Room database
                ideaRepository.saveIdea(
                    text = current.idea.trim(),
                    tag = current.tag
                )

                repository.createDraft(
                    idea = current.idea.trim(),
                    language = current.language,
                    template = current.template,
                    aspectRatio = current.aspectRatio,
                    durationSeconds = current.durationSeconds
                )
            }.onSuccess { project ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        createdProjectId = project.id
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        error = throwable.message ?: "تعذر إنشاء المشروع"
                    )
                }
            }
        }
    }
}
