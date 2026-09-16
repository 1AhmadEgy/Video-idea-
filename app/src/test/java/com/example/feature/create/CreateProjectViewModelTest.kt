package com.example.feature.create

import com.example.data.repository.IdeaRepository
import com.example.data.repository.ProjectRepository
import com.example.domain.model.Idea
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.model.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class CreateProjectViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeProjectRepository : ProjectRepository {
        val projects = mutableListOf<Project>()

        override fun observeProjects(): Flow<List<Project>> = MutableStateFlow(projects)
        override fun observeProject(projectId: String): Flow<Project?> = MutableStateFlow(projects.find { it.id == projectId })
        override suspend fun getProject(projectId: String): Project? = projects.find { it.id == projectId }
        override suspend fun getScenesForProject(projectId: String): List<Scene> = emptyList()

        override suspend fun createDraft(
            idea: String,
            language: String,
            template: String,
            aspectRatio: String,
            durationSeconds: Int
        ): Project {
            val project = Project(
                id = UUID.randomUUID().toString(),
                title = idea,
                idea = idea,
                language = language,
                template = template,
                aspectRatio = aspectRatio,
                durationSeconds = durationSeconds,
                status = ProjectStatus.DRAFT,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            projects.add(project)
            return project
        }

        override suspend fun updateProject(project: Project) {}
        override suspend fun duplicateProject(projectId: String): Project? = null
        override suspend fun restoreProject(project: Project, scenes: List<Scene>) {}
        override suspend fun deleteProject(projectId: String) {}
    }

    private class FakeIdeaRepository : IdeaRepository {
        val ideasFlow = MutableStateFlow<List<Idea>>(emptyList())

        override fun observeIdeas(onlyFavorites: Boolean, tag: String?): Flow<List<Idea>> {
            return ideasFlow.map { list ->
                var res = list
                if (onlyFavorites) res = res.filter { it.isFavorite }
                if (tag != null && tag != "الكل" && tag != "All") res = res.filter { it.tag == tag }
                res
            }
        }

        override suspend fun getAllIdeas(): List<Idea> = ideasFlow.value

        override suspend fun saveIdea(text: String, tag: String, script: String?): Idea? {
            val idea = Idea(
                id = UUID.randomUUID().toString(),
                text = text,
                tag = tag,
                script = script,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )
            ideasFlow.value = listOf(idea) + ideasFlow.value
            return idea
        }

        override suspend fun updateTag(id: String, tag: String) {
            ideasFlow.value = ideasFlow.value.map {
                if (it.id == id) it.copy(tag = tag) else it
            }
        }

        override suspend fun updateScript(id: String, script: String) {
            ideasFlow.value = ideasFlow.value.map {
                if (it.id == id) it.copy(script = script) else it
            }
        }

        override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
            ideasFlow.value = ideasFlow.value.map {
                if (it.id == id) it.copy(isFavorite = isFavorite) else it
            }
        }

        override suspend fun deleteIdea(id: String) {
            ideasFlow.value = ideasFlow.value.filterNot { it.id == id }
        }

        override suspend fun exportIdeasJson(): String {
            return """{"app":"Fikra Video","total":${ideasFlow.value.size}}"""
        }
    }

    private lateinit var fakeProjectRepository: FakeProjectRepository
    private lateinit var fakeIdeaRepository: FakeIdeaRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeProjectRepository = FakeProjectRepository()
        fakeIdeaRepository = FakeIdeaRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save current idea with custom tag persists to idea repository`() = runTest {
        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(CreateProjectAction.IdeaChanged("فكرة شرح تقني تعليمي"))
        viewModel.onAction(CreateProjectAction.TagChanged("Tutorial"))
        viewModel.onAction(CreateProjectAction.SaveCurrentIdea)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.savedIdeas.size)
        assertEquals("فكرة شرح تقني تعليمي", state.savedIdeas.first().text)
        assertEquals("Tutorial", state.savedIdeas.first().tag)
        assertNotNull(state.snackbarMessage)
    }

    @Test
    fun `details bottom sheet opens and closes correctly`() = runTest {
        val sample = Idea(id = "1", text = "فكرة سابقة", tag = "Vlog", isFavorite = false, createdAt = 1000L)
        fakeIdeaRepository.ideasFlow.value = listOf(sample)

        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(CreateProjectAction.OpenIdeaDetails(sample))
        advanceUntilIdle()

        assertEquals(sample, viewModel.uiState.value.selectedIdeaForDetails)

        viewModel.onAction(CreateProjectAction.CloseIdeaDetails)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedIdeaForDetails)
    }

    @Test
    fun `update idea tag updates repository and details state`() = runTest {
        val sample = Idea(id = "1", text = "فكرة سابقة", tag = "Shorts", isFavorite = false, createdAt = 1000L)
        fakeIdeaRepository.ideasFlow.value = listOf(sample)

        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(CreateProjectAction.OpenIdeaDetails(sample))
        viewModel.onAction(CreateProjectAction.UpdateIdeaTag("1", "Story"))
        advanceUntilIdle()

        assertEquals("Story", fakeIdeaRepository.ideasFlow.value.first().tag)
        assertEquals("Story", viewModel.uiState.value.selectedIdeaForDetails?.tag)
    }

    @Test
    fun `export backup json sets exported content in state`() = runTest {
        val sample = Idea(id = "1", text = "فكرة للنسخ الاحتياطي", tag = "Shorts", isFavorite = true, createdAt = 1000L)
        fakeIdeaRepository.ideasFlow.value = listOf(sample)

        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(CreateProjectAction.ExportBackupJson)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.exportedJsonContent)
        assertTrue(state.exportedJsonContent!!.contains("Fikra Video"))

        viewModel.onAction(CreateProjectAction.ClearExportedJson)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.exportedJsonContent)
    }

    @Test
    fun `delete confirmation flow requires confirm before deletion`() = runTest {
        val sample = Idea(id = "1", text = "فكرة للحذف", tag = "Shorts", isFavorite = false, createdAt = 1000L)
        fakeIdeaRepository.ideasFlow.value = listOf(sample)

        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Request delete
        viewModel.onAction(CreateProjectAction.RequestDeleteIdea(sample))
        advanceUntilIdle()

        assertEquals(sample, viewModel.uiState.value.ideaToDelete)
        assertEquals(1, viewModel.uiState.value.savedIdeas.size) // still not deleted

        // Dismiss
        viewModel.onAction(CreateProjectAction.DismissDeleteDialog)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.ideaToDelete)
        assertEquals(1, viewModel.uiState.value.savedIdeas.size)

        // Request delete again and confirm
        viewModel.onAction(CreateProjectAction.RequestDeleteIdea(sample))
        advanceUntilIdle()
        viewModel.onAction(CreateProjectAction.ConfirmDeleteIdea)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.ideaToDelete)
        assertEquals(0, viewModel.uiState.value.savedIdeas.size)
    }

    @Test
    fun `submit automatically saves idea with tag to database`() = runTest {
        val viewModel = CreateProjectViewModel(fakeProjectRepository, fakeIdeaRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.onAction(CreateProjectAction.IdeaChanged("فكرة جديدة تم إنشاؤها"))
        viewModel.onAction(CreateProjectAction.TagChanged("Promo"))
        viewModel.onAction(CreateProjectAction.Submit)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.createdProjectId)
        assertEquals(1, state.savedIdeas.size)
        assertEquals("فكرة جديدة تم إنشاؤها", state.savedIdeas.first().text)
        assertEquals("Promo", state.savedIdeas.first().tag)
    }
}
