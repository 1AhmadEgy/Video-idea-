package com.example.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.dao.ProjectDao
import com.example.core.database.dao.SceneDao
import com.example.core.database.entity.SceneEntity
import com.example.data.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao,
    private val aiRepository: AiRepository
) : ViewModel() {
    val projectId: String = savedStateHandle["projectId"] ?: ""

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                projectDao.observeProject(projectId),
                sceneDao.observeScenes(projectId)
            ) { project, scenes ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        project = project,
                        scenes = scenes
                    )
                }
            }.collect {}
        }
    }

    fun selectVoice(voice: String) {
        _uiState.update { it.copy(selectedVoice = voice) }
    }

    fun selectStyle(style: String) {
        _uiState.update { it.copy(selectedImageStyle = style) }
    }

    fun addScene() {
        viewModelScope.launch {
            val currentScenes = _uiState.value.scenes
            val scene = SceneEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                position = currentScenes.size,
                narration = "",
                visualPrompt = "",
                durationSeconds = 5,
                status = "DRAFT"
            )
            sceneDao.upsert(scene)
        }
    }

    fun updateScene(scene: SceneEntity) {
        viewModelScope.launch {
            sceneDao.upsert(scene)
        }
    }

    fun deleteScene(scene: SceneEntity) {
        viewModelScope.launch {
            sceneDao.deleteById(scene.id)
            // Re-order remaining scenes
            val currentScenes = _uiState.value.scenes.filter { it.id != scene.id }.sortedBy { it.position }
            val updatedScenes = currentScenes.mapIndexed { index, s -> s.copy(position = index) }
            sceneDao.upsertAll(updatedScenes)
        }
    }

    fun updateProjectIdea(idea: String) {
        viewModelScope.launch {
            val project = _uiState.value.project ?: return@launch
            projectDao.upsert(project.copy(idea = idea))
        }
    }

    fun generateScriptWithAi() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            try {
                val project = _uiState.value.project ?: throw IllegalStateException("Project not loaded")
                val response = aiRepository.generateScript(
                    projectId = project.id,
                    idea = project.idea
                )
                
                // Clear existing scenes
                sceneDao.deleteByProjectId(project.id)
                
                // Add new scenes
                val newScenes = response.scenes.mapIndexed { index, scene ->
                    SceneEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = project.id,
                        position = index,
                        narration = scene.narration,
                        visualPrompt = scene.visualPrompt,
                        durationSeconds = scene.durationSeconds,
                        status = "DRAFT"
                    )
                }
                sceneDao.upsertAll(newScenes)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "حدث خطأ أثناء توليد السيناريو") }
            } finally {
                _uiState.update { it.copy(isGenerating = false) }
            }
        }
    }

    fun saveSettingsAndContinue(onContinue: () -> Unit) {
        onContinue()
    }
}
