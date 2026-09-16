package com.example.feature.storyboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SceneRepository
import com.example.domain.usecase.GenerateScenesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StoryboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val sceneRepository: SceneRepository,
    private val generateScenesUseCase: GenerateScenesUseCase
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])
    
    private val _isGenerating = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<StoryboardUiState> = combine(
        projectRepository.observeProject(projectId),
        sceneRepository.observeScenes(projectId),
        _isGenerating,
        _error
    ) { project, scenes, isGenerating, error ->
        StoryboardUiState(
            project = project,
            scenes = scenes,
            isLoading = project == null,
            isGenerating = isGenerating,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StoryboardUiState(isLoading = true)
    )

    fun onAction(action: StoryboardAction) {
        when (action) {
            StoryboardAction.GenerateScript -> generateScript()
            is StoryboardAction.AddScene -> {
                viewModelScope.launch {
                    sceneRepository.createScene(
                        projectId = projectId,
                        narration = "",
                        visualPrompt = "",
                        position = action.position,
                        durationSeconds = 5
                    )
                }
            }
            is StoryboardAction.DeleteScene -> {
                viewModelScope.launch {
                    sceneRepository.deleteScene(action.sceneId)
                }
            }
            is StoryboardAction.UpdateScene -> {
                viewModelScope.launch {
                    sceneRepository.updateSceneContent(
                        sceneId = action.sceneId,
                        narration = action.narration,
                        visualPrompt = action.visualPrompt,
                        durationSeconds = action.durationSeconds
                    )
                }
            }
            StoryboardAction.Refresh -> {
                _error.value = null
            }
        }
    }

    private fun generateScript() {
        val project = uiState.value.project ?: return
        
        viewModelScope.launch {
            _isGenerating.value = true
            _error.value = null
            
            try {
                generateScenesUseCase(
                    projectId = project.id,
                    idea = project.idea,
                    language = project.language,
                    template = project.template,
                    aspectRatio = project.aspectRatio,
                    durationSeconds = project.durationSeconds,
                    sceneCount = 5
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "حدث خطأ أثناء توليد المشاهد"
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
