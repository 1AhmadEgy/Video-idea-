package com.example.data.mapper

import com.example.core.network.JobResponse
import com.example.data.repository.JobResult
import com.example.data.repository.JobStatus

fun JobResponse.toDomain(): JobResult {
    return JobResult(
        jobId = jobId,
        projectId = projectId,
        type = type,
        status = status.toJobStatus(),
        progress = progress,
        stage = stage,
        message = message,
        outputUrl = outputUrl,
        error = error
    )
}

fun String.toJobStatus(): JobStatus {
    return runCatching {
        JobStatus.valueOf(this.uppercase())
    }.getOrDefault(JobStatus.FAILED)
}
