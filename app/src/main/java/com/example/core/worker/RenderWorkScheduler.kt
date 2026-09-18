package com.example.core.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenderWorkScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun startFinalRender(projectId: String, sceneIds: List<String>) {
        val input = workDataOf(
            WorkerConstants.PROJECT_ID to projectId,
            "scene_ids" to sceneIds.toTypedArray()
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val imagesWork = OneTimeWorkRequestBuilder<GenerateImagesWorker>()
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        val audioWork = OneTimeWorkRequestBuilder<GenerateAudioWorker>()
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        val renderWork = OneTimeWorkRequestBuilder<FinalRenderWorker>()
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            getUniqueWorkName(projectId),
            ExistingWorkPolicy.KEEP,
            imagesWork
        ).then(audioWork).then(renderWork).enqueue()
    }

    fun cancelFinalRender(projectId: String) {
        workManager.cancelUniqueWork(getUniqueWorkName(projectId))
    }

    fun getUniqueWorkName(projectId: String): String {
        return "${WorkerConstants.WORK_NAME_PREFIX}$projectId"
    }
}
