package com.example

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.core.designsystem.FikraVideoTheme
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.feature.projects.components.ProjectCard
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleProject = Project(
      id = "demo-1",
      title = "فيديو تعريفي بالذكاء الاصطناعي",
      idea = "شرح مبسط لقدرات توليد الفيديو عبر الذكاء الاصطناعي مع صور واقعية",
      language = "ar",
      template = "modern",
      aspectRatio = "9:16",
      durationSeconds = 30,
      status = ProjectStatus.READY_TO_RENDER,
      createdAt = 1700000000000L,
      updatedAt = 1700000000000L
    )

    composeTestRule.setContent {
      FikraVideoTheme {
        ProjectCard(
          project = sampleProject,
          onClick = {},
          onEdit = {},
          onDuplicate = {},
          onDelete = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
