package com.example.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jobs",
    indices = [
        Index(value = ["project_id"]),
        Index(value = ["status"])
    ]
)
data class JobEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    @ColumnInfo(name = "scene_id")
    val sceneId: String?,
    @ColumnInfo(name = "job_type")
    val jobType: String,
    val status: String,
    val progress: Int,
    val stage: String?,
    @ColumnInfo(name = "output_url")
    val outputUrl: String?,
    @ColumnInfo(name = "error_message")
    val errorMessage: String?,
    val attempts: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
