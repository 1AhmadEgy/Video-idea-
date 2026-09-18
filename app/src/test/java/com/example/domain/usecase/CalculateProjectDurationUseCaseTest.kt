package com.example.domain.usecase

import com.example.domain.model.Scene
import com.example.domain.model.SceneStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class CalculateProjectDurationUseCaseTest {

    private val useCase = CalculateProjectDurationUseCase()

    @Test
    fun `empty scenes returns zero duration`() {
        val duration = useCase(emptyList())
        assertEquals(0, duration)
    }

    @Test
    fun `sum duration correctly accumulates positive durations`() {
        val scenes = listOf(
            createScene(15),
            createScene(25),
            createScene(30)
        )
        val duration = useCase(scenes)
        assertEquals(70, duration)
    }

    @Test
    fun `negative durations are clamped to zero`() {
        val scenes = listOf(
            createScene(20),
            createScene(-10),
            createScene(15)
        )
        val duration = useCase(scenes)
        assertEquals(35, duration)
    }

    private fun createScene(duration: Int): Scene {
        return Scene(
            id = UUID.randomUUID().toString(),
            projectId = "test-project",
            position = 0,
            narration = "Narration",
            visualPrompt = "Prompt",
            durationSeconds = duration,
            status = SceneStatus.DRAFT
        )
    }
}
