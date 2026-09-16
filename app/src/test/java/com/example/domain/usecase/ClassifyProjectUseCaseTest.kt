package com.example.domain.usecase

import com.example.domain.model.Scene
import com.example.domain.model.SceneStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.UUID

@RunWith(Parameterized::class)
class ClassifyProjectUseCaseTest(
    private val caseName: String,
    private val scenes: List<Scene>,
    private val expected: String
) {
    private val useCase = ClassifyProjectUseCase()

    @Test
    fun classification_is_correct() {
        assertEquals(
            caseName,
            expected,
            useCase(scenes)
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    "empty scenes",
                    emptyList<Scene>(),
                    "draft"
                ),
                arrayOf(
                    "no narration or visual prompt",
                    listOf(
                        scene("", "", 30),
                        scene("", "", 40)
                    ),
                    "draft"
                ),
                arrayOf(
                    "some scenes filled",
                    listOf(
                        scene("مقدمة الفيديو", "لقطة سينمائية", 30),
                        scene("", "", 40)
                    ),
                    "active"
                ),
                arrayOf(
                    "all scenes filled with duration",
                    listOf(
                        scene("مقدمة الفيديو", "لقطة سينمائية", 30),
                        scene("المحتوى الرئيسي", "شرح تفصيلي", 40)
                    ),
                    "completed"
                ),
                arrayOf(
                    "all scenes filled with zero duration",
                    listOf(
                        scene("نص المشهد", "وصف المشهد", 0)
                    ),
                    "active"
                ),
                arrayOf(
                    "negative duration is clamped",
                    listOf(
                        scene("نص المشهد", "وصف المشهد", -10)
                    ),
                    "active"
                )
            )
        }

        private fun scene(
            narration: String,
            visualPrompt: String,
            duration: Int
        ): Scene {
            return Scene(
                id = UUID.randomUUID().toString(),
                projectId = "project-1",
                position = 0,
                narration = narration,
                visualPrompt = visualPrompt,
                durationSeconds = duration,
                status = SceneStatus.DRAFT
            )
        }
    }
}
