package com.example.data.mapper

import com.example.core.database.entity.ProjectEntity
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        title = title,
        idea = idea,
        language = language,
        template = template,
        aspectRatio = aspectRatio,
        durationSeconds = durationSeconds,
        status = runCatching {
            ProjectStatus.valueOf(status)
        }.getOrDefault(ProjectStatus.DRAFT),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        title = title,
        idea = idea,
        language = language,
        template = template,
        aspectRatio = aspectRatio,
        durationSeconds = durationSeconds,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
