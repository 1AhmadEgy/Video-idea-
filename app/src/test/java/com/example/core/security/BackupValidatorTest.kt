package com.example.core.security

import org.junit.Test

class BackupValidatorTest {

    @Test
    fun `validate passes for valid consistent backup`() {
        val backup = BackupPayload(
            formatVersion = 1,
            createdAt = System.currentTimeMillis(),
            projects = listOf(
                BackupProjectData(id = "proj-1", title = "مشروع 1")
            ),
            scenes = listOf(
                BackupSceneData(id = "scene-1", projectId = "proj-1", position = 0)
            ),
            ideas = listOf(
                BackupIdeaData(id = "idea-1", text = "فكرة 1")
            )
        )

        BackupValidator.validate(backup)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate throws when scene references non-existent project`() {
        val backup = BackupPayload(
            formatVersion = 1,
            createdAt = System.currentTimeMillis(),
            projects = listOf(
                BackupProjectData(id = "proj-1", title = "مشروع 1")
            ),
            scenes = listOf(
                BackupSceneData(id = "scene-1", projectId = "non-existent-proj", position = 0)
            )
        )

        BackupValidator.validate(backup)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate throws when project ids are duplicated`() {
        val backup = BackupPayload(
            formatVersion = 1,
            createdAt = System.currentTimeMillis(),
            projects = listOf(
                BackupProjectData(id = "proj-1", title = "مشروع 1"),
                BackupProjectData(id = "proj-1", title = "مشروع مكرر")
            )
        )

        BackupValidator.validate(backup)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate throws when format version is unsupported`() {
        val backup = BackupPayload(
            formatVersion = 999,
            createdAt = System.currentTimeMillis()
        )

        BackupValidator.validate(backup)
    }
}
