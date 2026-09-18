package com.example.domain.usecase

import com.example.domain.model.Scene
import com.example.domain.model.SceneStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class ReorderScenesUseCaseTest {

    private val useCase = ReorderScenesUseCase()

    @Test
    fun `reorder swaps scene order and updates positions`() {
        val s1 = createScene("Scene 1", 0)
        val s2 = createScene("Scene 2", 1)
        val s3 = createScene("Scene 3", 2)

        val reordered = useCase(listOf(s1, s2, s3), fromIndex = 0, toIndex = 2)

        assertEquals(3, reordered.size)
        assertEquals("Scene 2", reordered[0].narration)
        assertEquals(0, reordered[0].position)
        assertEquals("Scene 3", reordered[1].narration)
        assertEquals(1, reordered[1].position)
        assertEquals("Scene 1", reordered[2].narration)
        assertEquals(2, reordered[2].position)
    }

    @Test
    fun `invalid indices return original list`() {
        val s1 = createScene("Scene 1", 0)
        val original = listOf(s1)

        val result = useCase(original, fromIndex = -1, toIndex = 5)
        assertEquals(original, result)
    }

    private fun createScene(narration: String, position: Int): Scene {
        return Scene(
            id = UUID.randomUUID().toString(),
            projectId = "test-project",
            position = position,
            narration = narration,
            visualPrompt = "Prompt",
            durationSeconds = 10,
            status = SceneStatus.DRAFT
        )
    }
}
