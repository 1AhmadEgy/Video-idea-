package com.example.feature.projects

import com.example.data.repository.ProjectCleanupRepository
import com.example.data.repository.ProjectRepository
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import com.example.feature.projects.components.ProjectSortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeProjectRepository : ProjectRepository {
        val projectsFlow = MutableStateFlow<List<Project>>(emptyList())
        val scenesMap = mutableMapOf<String, List<Scene>>()

        override fun observeProjects(): Flow<List<Project>> = projectsFlow

        override fun observeProject(projectId: String): Flow<Project?> =
            MutableStateFlow(projectsFlow.value.find { it.id == projectId })

        override suspend fun getProject(projectId: String): Project? =
            projectsFlow.value.find { it.id == projectId }

        override suspend fun getScenesForProject(projectId: String): List<Scene> =
            scenesMap[projectId] ?: emptyList()

        override suspend fun createDraft(
            idea: String,
            language: String,
            template: String,
            aspectRatio: String,
            durationSeconds: Int
        ): Project {
            val now = System.currentTimeMillis()
            val project = Project(
                id = UUID.randomUUID().toString(),
                title = idea,
                idea = idea,
                language = language,
                template = template,
                aspectRatio = aspectRatio,
                durationSeconds = durationSeconds,
                status = ProjectStatus.DRAFT,
                createdAt = now,
                updatedAt = now
            )
            projectsFlow.value = projectsFlow.value + project
            return project
        }

        override suspend fun updateProject(project: Project) {
            projectsFlow.value = projectsFlow.value.map {
                if (it.id == project.id) project else it
            }
        }

        override suspend fun duplicateProject(projectId: String): Project? {
            val source = getProject(projectId) ?: return null
            val now = System.currentTimeMillis()
            val copy = source.copy(
                id = UUID.randomUUID().toString(),
                title = "${source.title} (نسخة)",
                createdAt = now,
                updatedAt = now
            )
            projectsFlow.value = projectsFlow.value + copy
            return copy
        }

        override suspend fun restoreProject(project: Project, scenes: List<Scene>) {
            projectsFlow.value = projectsFlow.value + project
            scenesMap[project.id] = scenes
        }

        override suspend fun deleteProject(projectId: String) {
            projectsFlow.value = projectsFlow.value.filterNot { it.id == projectId }
            scenesMap.remove(projectId)
        }
    }

    private class FakeProjectCleanupRepository : ProjectCleanupRepository {
        val deletedIds = mutableListOf<String>()
        override suspend fun deleteProjectCompletely(projectId: String) {
            deletedIds.add(projectId)
        }
    }

    private lateinit var fakeRepository: FakeProjectRepository
    private lateinit var fakeCleanupRepository: FakeProjectCleanupRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeProjectRepository()
        fakeCleanupRepository = FakeProjectCleanupRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search filters projects by title and idea`() = runTest {
        val p1 = Project(
            id = "1",
            title = "مشروع الذكاء الاصطناعي",
            idea = "فكرة عن البرمجة",
            language = "ar",
            template = "modern",
            aspectRatio = "9:16",
            durationSeconds = 30,
            status = ProjectStatus.DRAFT,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val p2 = Project(
            id = "2",
            title = "طبخ حلويات",
            idea = "وصفة كيك شوكولاتة",
            language = "ar",
            template = "cinematic",
            aspectRatio = "16:9",
            durationSeconds = 60,
            status = ProjectStatus.COMPLETED,
            createdAt = 2000L,
            updatedAt = 2000L
        )

        fakeRepository.projectsFlow.value = listOf(p1, p2)

        val viewModel = ProjectsViewModel(fakeRepository, fakeCleanupRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals(2, state.projects.size)

        // Search for "ذكاء"
        viewModel.onAction(ProjectsAction.SetSearchQuery("ذكاء"))
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals(1, state.projects.size)
        assertEquals("مشروع الذكاء الاصطناعي", state.projects.first().title)

        // Search for idea "كيك"
        viewModel.onAction(ProjectsAction.SetSearchQuery("كيك"))
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals(1, state.projects.size)
        assertEquals("طبخ حلويات", state.projects.first().title)
    }

    @Test
    fun `filter by status filters correctly`() = runTest {
        val p1 = Project(
            id = "1",
            title = "مسودة 1",
            idea = "فكرة",
            language = "ar",
            template = "modern",
            aspectRatio = "9:16",
            durationSeconds = 30,
            status = ProjectStatus.DRAFT,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val p2 = Project(
            id = "2",
            title = "مشروع مكتمل",
            idea = "فكرة مكتملة",
            language = "ar",
            template = "cinematic",
            aspectRatio = "16:9",
            durationSeconds = 60,
            status = ProjectStatus.COMPLETED,
            createdAt = 2000L,
            updatedAt = 2000L
        )

        fakeRepository.projectsFlow.value = listOf(p1, p2)

        val viewModel = ProjectsViewModel(fakeRepository, fakeCleanupRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(ProjectsAction.SetStatusFilter(ProjectStatus.COMPLETED))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.projects.size)
        assertEquals("مشروع مكتمل", state.projects.first().title)
    }

    @Test
    fun `sorting by name works ascending`() = runTest {
        val p1 = Project(
            id = "1",
            title = "باء",
            idea = "فكرة",
            language = "ar",
            template = "modern",
            aspectRatio = "9:16",
            durationSeconds = 30,
            status = ProjectStatus.DRAFT,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val p2 = Project(
            id = "2",
            title = "ألف",
            idea = "فكرة",
            language = "ar",
            template = "cinematic",
            aspectRatio = "16:9",
            durationSeconds = 60,
            status = ProjectStatus.COMPLETED,
            createdAt = 2000L,
            updatedAt = 2000L
        )

        fakeRepository.projectsFlow.value = listOf(p1, p2)

        val viewModel = ProjectsViewModel(fakeRepository, fakeCleanupRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(ProjectsAction.SetSortType(ProjectSortType.NAME))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("ألف", state.projects.first().title)
        assertEquals("باء", state.projects.last().title)
    }

    @Test
    fun `delete project calls cleanup repository`() = runTest {
        val p1 = Project(
            id = "p-123",
            title = "مشروع للحذف",
            idea = "فكرة",
            language = "ar",
            template = "modern",
            aspectRatio = "9:16",
            durationSeconds = 30,
            status = ProjectStatus.DRAFT,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        fakeRepository.projectsFlow.value = listOf(p1)

        val viewModel = ProjectsViewModel(fakeRepository, fakeCleanupRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(ProjectsAction.DeleteProject(p1))
        advanceUntilIdle()

        assertTrue(fakeCleanupRepository.deletedIds.contains("p-123"))
    }
}
