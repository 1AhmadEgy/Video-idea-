package com.example.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scenes",
    indices = [
        Index(value = ["project_id"]),
        Index(value = ["project_id", "position"], unique = true)
    ]
)
data class SceneEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    val position: Int,
    val narration: String,
    @ColumnInfo(name = "visual_prompt")
    val visualPrompt: String,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    val status: String
)
