package com.example.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.repository.MediaGenerationRepository
import com.example.data.repository.JobStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import java.io.IOException

@HiltWorker
class GenerateAudioWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MediaGenerationRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val projectId = inputData.getString(WorkerConstants.PROJECT_ID) ?: return Result.failure()

        return try {
            val job = repository.requestAudioGeneration(projectId)
            val completed = repository.waitForJob(jobId = job.jobId) { progress ->
                setProgress(
                    workDataOf(
                        WorkerConstants.STAGE to "generate_audio",
                        WorkerConstants.PROGRESS to progress.progress,
                        "job_id" to progress.jobId
                    )
                )
            }

            when (completed.status) {
                JobStatus.COMPLETED -> Result.success()
                JobStatus.FAILED -> Result.failure(workDataOf(WorkerConstants.RESULT_MESSAGE to (completed.error ?: "فشل توليد الصوت")))
                else -> Result.failure()
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: IOException) {
            Result.retry()
        } catch (error: Exception) {
            Result.failure(workDataOf(WorkerConstants.RESULT_MESSAGE to (error.message ?: "تعذر الاتصال بخدمة الصوت")))
        }
    }
}
