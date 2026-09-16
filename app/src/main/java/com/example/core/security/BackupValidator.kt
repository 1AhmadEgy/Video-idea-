package com.example.core.security

import kotlinx.serialization.Serializable

@Serializable
data class BackupProjectData(
    val id: String,
    val title: String,
    val idea: String = "",
    val language: String = "ar",
    val template: String = "educational",
    val status: String = "DRAFT",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class BackupSceneData(
    val id: String,
    val projectId: String,
    val position: Int,
    val narration: String = "",
    val visualPrompt: String = "",
    val durationSeconds: Int = 5,
    val status: String = "DRAFT"
)

@Serializable
data class BackupIdeaData(
    val id: String,
    val text: String,
    val tag: String = "Shorts",
    val script: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = 0L
)

@Serializable
data class BackupPayload(
    val formatVersion: Int,
    val createdAt: Long,
    val projects: List<BackupProjectData> = emptyList(),
    val scenes: List<BackupSceneData> = emptyList(),
    val ideas: List<BackupIdeaData> = emptyList()
)

object BackupValidator {

    const val CURRENT_VERSION = 1
    const val MAX_PROJECTS = 10_000
    const val MAX_SCENES = 50_000
    const val MAX_IDEAS = 20_000

    fun validate(backup: BackupPayload) {
        require(backup.formatVersion in 1..CURRENT_VERSION) {
            "Unsupported backup version: ${backup.formatVersion}"
        }

        require(backup.projects.size <= MAX_PROJECTS) {
            "Backup exceeds maximum allowed projects limit"
        }
        require(backup.scenes.size <= MAX_SCENES) {
            "Backup exceeds maximum allowed scenes limit"
        }
        require(backup.ideas.size <= MAX_IDEAS) {
            "Backup exceeds maximum allowed ideas limit"
        }

        requireUnique(backup.projects.map { it.id }, "project")
        requireUnique(backup.scenes.map { it.id }, "scene")
        requireUnique(backup.ideas.map { it.id }, "idea")

        val projectIds = backup.projects.map { it.id }.toSet()
        require(backup.scenes.all { it.projectId in projectIds }) {
            "Backup contains scenes with non-existent project references"
        }
    }

    private fun requireUnique(ids: List<String>, entityName: String) {
        require(ids.size == ids.toSet().size) {
            "Duplicate IDs found in backup $entityName entries"
        }
    }
}
