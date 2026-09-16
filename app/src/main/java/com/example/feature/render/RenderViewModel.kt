package com.example.feature.render

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.core.worker.WorkerConstants
import com.example.domain.usecase.StartFinalRenderUseCase
import com.example.core.database.dao.SceneDao
import com.example.core.media.VideoDownloader
import com.example.core.media.MediaStoreSaver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class RenderViewModel @Inject constructor(
    application: Application,
    private val startFinalRender: StartFinalRenderUseCase,
    private val sceneDao: SceneDao,
    private val videoDownloader: VideoDownloader,
    private val mediaStoreSaver: MediaStoreSaver
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RenderUiState())
    val uiState: StateFlow<RenderUiState> = _uiState.asStateFlow()
    private val workManager = WorkManager.getInstance(application)

    fun startRender(projectId: String) {
        _uiState.update { RenderUiState(isRunning = true, progress = 0, stage = "queued") }
        viewModelScope.launch {
            val scenes = sceneDao.observeScenes(projectId).firstOrNull() ?: emptyList()
            val sceneIds = scenes.map { it.id }
            startFinalRender(projectId = projectId, sceneIds = sceneIds)
            observeRender(projectId)
        }
    }

    fun cancelRender(projectId: String) {
        workManager.cancelUniqueWork("${WorkerConstants.WORK_NAME_PREFIX}$projectId")
        _uiState.update { it.copy(isRunning = false, stage = "cancelled") }
    }

    fun observeRender(projectId: String) {
        val workName = "${WorkerConstants.WORK_NAME_PREFIX}$projectId"
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                updateState(workInfos)
            }
        }
    }

    fun downloadVideo() {
        val url = _uiState.value.outputVideoUrl ?: return
        videoDownloader.download(
            url = url,
            fileName = "fikra_${System.currentTimeMillis()}.mp4"
        )
    }

    fun saveVideo() {
        val url = _uiState.value.outputVideoUrl ?: return
        viewModelScope.launch {
            val savedUri = mediaStoreSaver.saveVideo(
                videoUrl = url,
                fileName = "fikra_${System.currentTimeMillis()}.mp4"
            )
            if (savedUri == null) {
                _uiState.update {
                    it.copy(errorMessage = "تعذر حفظ الفيديو")
                }
            }
        }
    }

    private fun updateState(workInfos: List<WorkInfo>) {
        if (workInfos.isEmpty()) return
        val current = workInfos.firstOrNull { it.state == WorkInfo.State.RUNNING } ?: workInfos.last()
        val progress = current.progress.getInt(WorkerConstants.PROGRESS, 0)
        val stage = current.progress.getString(WorkerConstants.STAGE)

        when (current.state) {
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.BLOCKED -> {
                _uiState.update {
                    it.copy(
                        isRunning = true,
                        progress = progress,
                        stage = stage,
                        errorMessage = null
                    )
                }
            }
            WorkInfo.State.SUCCEEDED -> {
                val output = current.outputData.getString(WorkerConstants.OUTPUT_VIDEO_URI)
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        progress = 100,
                        stage = "completed",
                        outputVideoUrl = output,
                        errorMessage = null
                    )
                }
            }
            WorkInfo.State.FAILED -> {
                val error = current.outputData.getString(WorkerConstants.RESULT_MESSAGE)
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        stage = "failed",
                        errorMessage = error ?: "فشل الرندر النهائي"
                    )
                }
            }
            WorkInfo.State.CANCELLED -> {
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        stage = "cancelled",
                        errorMessage = "تم إلغاء العملية"
                    )
                }
            }
        }
    }
}
