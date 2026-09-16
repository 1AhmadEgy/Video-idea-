package com.example.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.core.media.OrphanMediaCleaner
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class CleanupMediaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cleaner: OrphanMediaCleaner
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            cleaner.clean()
            Result.success()
        } catch (error: IOException) {
            Result.retry()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "media_cleanup"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val request = OneTimeWorkRequestBuilder<CleanupMediaWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
