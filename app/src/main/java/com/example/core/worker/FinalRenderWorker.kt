package com.example.core.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.core.notification.RenderNotificationManager
import com.example.data.repository.JobStatus
import com.example.data.repository.MediaGenerationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import java.io.IOException

@HiltWorker
class FinalRenderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MediaGenerationRepository,
    private val notificationManager: RenderNotificationManager
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        notificationManager.createChannel()
        return ForegroundInfo(
            RenderNotificationManager.NOTIFICATION_ID,
            notificationManager.createNotification(title = "جاري تنفيذ الرندر", progress = 0)
        )
    }

    override suspend fun doWork(): Result {
        val projectId = inputData.getString(WorkerConstants.PROJECT_ID) ?: return Result.failure()
        
        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) { }

        return try {
            val job = repository.requestFinalRender(projectId)
            val completed = repository.waitForJob(jobId = job.jobId) { progress ->
                setProgress(
                    workDataOf(
                        WorkerConstants.STAGE to "final_render",
                        WorkerConstants.PROGRESS to progress.progress,
                        "job_id" to progress.jobId
                    )
                )
                try {
                    val manager = NotificationManagerCompat.from(applicationContext)
                    if (androidx.core.content.ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        manager.notify(RenderNotificationManager.NOTIFICATION_ID, notificationManager.createNotification(title = "جارٍ رندر الفيديو", progress = progress.progress))
                    }
                } catch (e: Exception) { }
            }

            when (completed.status) {
                JobStatus.COMPLETED -> {
                    notificationManager.notifyCompleted()
                    Result.success(workDataOf(WorkerConstants.OUTPUT_VIDEO_URI to (completed.outputUrl ?: "")))
                }
                JobStatus.FAILED -> Result.failure(workDataOf(WorkerConstants.RESULT_MESSAGE to (completed.error ?: "فشل الرندر")))
                else -> Result.failure()
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: IOException) {
            Result.retry()
        } catch (error: Exception) {
            Result.failure(workDataOf(WorkerConstants.RESULT_MESSAGE to (error.message ?: "تعذر تنفيذ الرندر")))
        }
    }
}
