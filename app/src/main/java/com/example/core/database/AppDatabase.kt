package com.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.database.dao.AssetDao
import com.example.core.database.dao.IdeaDao
import com.example.core.database.dao.JobDao
import com.example.core.database.dao.ProjectDao
import com.example.core.database.dao.SceneDao
import com.example.core.database.entity.AssetEntity
import com.example.core.database.entity.IdeaEntity
import com.example.core.database.entity.JobEntity
import com.example.core.database.entity.ProjectEntity
import com.example.core.database.entity.SceneEntity

@Database(
    entities = [
        ProjectEntity::class,
        SceneEntity::class,
        AssetEntity::class,
        JobEntity::class,
        IdeaEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun assetDao(): AssetDao
    abstract fun jobDao(): JobDao
    abstract fun ideaDao(): IdeaDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE jobs ADD COLUMN stage TEXT")
        db.execSQL("ALTER TABLE jobs ADD COLUMN output_url TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ideas (
                id TEXT NOT NULL PRIMARY KEY,
                text TEXT NOT NULL,
                is_favorite INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ideas ADD COLUMN tag TEXT NOT NULL DEFAULT 'Shorts'")
        db.execSQL("ALTER TABLE ideas ADD COLUMN script TEXT")
    }
}
