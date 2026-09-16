package com.example.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    indices = [
        Index(value = ["project_id"]),
        Index(value = ["scene_id"])
    ]
)
data class AssetEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    @ColumnInfo(name = "scene_id")
    val sceneId: String?,
    @ColumnInfo(name = "asset_type")
    val assetType: String,
    @ColumnInfo(name = "storage_uri")
    val storageUri: String,
    @ColumnInfo(name = "mime_type")
    val mimeType: String,
    @ColumnInfo(name = "file_size")
    val fileSize: Long,
    val checksum: String?,
    val provider: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
