package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.database.entity.IdeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {

    @Query("SELECT * FROM ideas ORDER BY created_at DESC")
    fun observeAllIdeas(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun observeFavoriteIdeas(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE tag = :tag ORDER BY created_at DESC")
    fun observeIdeasByTag(tag: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas ORDER BY created_at DESC")
    suspend fun getAllIdeas(): List<IdeaEntity>

    @Query("SELECT * FROM ideas WHERE id = :id")
    suspend fun getIdeaById(id: String): IdeaEntity?

    @Query("SELECT * FROM ideas WHERE text = :text LIMIT 1")
    suspend fun getIdeaByText(text: String): IdeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(idea: IdeaEntity)

    @Update
    suspend fun update(idea: IdeaEntity)

    @Query("UPDATE ideas SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE ideas SET tag = :tag WHERE id = :id")
    suspend fun updateTag(id: String, tag: String)

    @Query("UPDATE ideas SET script = :script WHERE id = :id")
    suspend fun updateScript(id: String, script: String)

    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteById(id: String)
}
