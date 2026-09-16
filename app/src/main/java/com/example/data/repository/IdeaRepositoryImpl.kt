package com.example.data.repository

import com.example.core.database.dao.IdeaDao
import com.example.core.database.entity.IdeaEntity
import com.example.domain.model.Idea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class IdeaRepositoryImpl @Inject constructor(
    private val ideaDao: IdeaDao
) : IdeaRepository {

    override fun observeIdeas(onlyFavorites: Boolean, tag: String?): Flow<List<Idea>> {
        val baseFlow = when {
            tag != null && tag.isNotBlank() && tag != "الكل" && tag != "All" -> {
                ideaDao.observeIdeasByTag(tag)
            }
            onlyFavorites -> {
                ideaDao.observeFavoriteIdeas()
            }
            else -> {
                ideaDao.observeAllIdeas()
            }
        }

        return baseFlow.map { list ->
            var filtered = list
            if (onlyFavorites && (tag != null && tag.isNotBlank() && tag != "الكل" && tag != "All")) {
                filtered = filtered.filter { it.isFavorite }
            }
            filtered.map { it.toDomain() }
        }
    }

    override suspend fun getAllIdeas(): List<Idea> {
        return ideaDao.getAllIdeas().map { it.toDomain() }
    }

    override suspend fun saveIdea(text: String, tag: String, script: String?): Idea? {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return null

        val normalizedTag = if (tag.isBlank()) "Shorts" else tag

        val existing = ideaDao.getIdeaByText(trimmed)
        val entity = if (existing != null) {
            val updated = existing.copy(
                createdAt = System.currentTimeMillis(),
                tag = if (normalizedTag != "Shorts") normalizedTag else existing.tag,
                script = script ?: existing.script
            )
            ideaDao.update(updated)
            updated
        } else {
            val newEntity = IdeaEntity(
                id = UUID.randomUUID().toString(),
                text = trimmed,
                tag = normalizedTag,
                script = script,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )
            ideaDao.insert(newEntity)
            newEntity
        }
        return entity.toDomain()
    }

    override suspend fun updateTag(id: String, tag: String) {
        ideaDao.updateTag(id, tag)
    }

    override suspend fun updateScript(id: String, script: String) {
        ideaDao.updateScript(id, script)
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        ideaDao.setFavorite(id, isFavorite)
    }

    override suspend fun deleteIdea(id: String) {
        ideaDao.deleteById(id)
    }

    override suspend fun exportIdeasJson(): String {
        val ideas = getAllIdeas()
        val jsonArray = JSONArray()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

        for (idea in ideas) {
            val obj = JSONObject().apply {
                put("id", idea.id)
                put("text", idea.text)
                put("tag", idea.tag)
                put("isFavorite", idea.isFavorite)
                put("createdAt", idea.createdAt)
                put("dateFormatted", dateFormat.format(Date(idea.createdAt)))
                put("script", idea.script ?: JSONObject.NULL)
            }
            jsonArray.put(obj)
        }

        val root = JSONObject().apply {
            put("app", "Fikra Video")
            put("backupVersion", 1)
            put("exportedAt", System.currentTimeMillis())
            put("exportedAtFormatted", dateFormat.format(Date()))
            put("totalCount", ideas.size)
            put("ideas", jsonArray)
        }

        return root.toString(2)
    }

    private fun IdeaEntity.toDomain() = Idea(
        id = id,
        text = text,
        tag = tag,
        script = script,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}
