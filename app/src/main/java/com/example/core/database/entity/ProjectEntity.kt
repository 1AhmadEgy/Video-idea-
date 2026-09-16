package com.example.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val idea: String,
    val language: String,
    val template: String,
    @ColumnInfo(name = "aspect_ratio")
    val aspectRatio: String,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    val status: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
