package com.example.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.core.database.entity.ProjectEntity
import com.example.core.database.entity.SceneEntity

data class ProjectWithScenes(
    @Embedded
    val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "project_id"
    )
    val scenes: List<SceneEntity>
)
