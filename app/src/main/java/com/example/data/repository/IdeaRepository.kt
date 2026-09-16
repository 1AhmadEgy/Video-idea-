package com.example.data.repository

import com.example.domain.model.Idea
import kotlinx.coroutines.flow.Flow

interface IdeaRepository {
    fun observeIdeas(onlyFavorites: Boolean = false, tag: String? = null): Flow<List<Idea>>
    suspend fun getAllIdeas(): List<Idea>
    suspend fun saveIdea(text: String, tag: String = "Shorts", script: String? = null): Idea?
    suspend fun updateTag(id: String, tag: String)
    suspend fun updateScript(id: String, script: String)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    suspend fun deleteIdea(id: String)
    suspend fun exportIdeasJson(): String
}
