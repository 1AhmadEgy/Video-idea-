package com.example.core.di

import android.content.Context
import androidx.room.Room
import com.example.core.database.AppDatabase
import com.example.core.database.dao.AssetDao
import com.example.core.database.dao.IdeaDao
import com.example.core.database.dao.JobDao
import com.example.core.database.dao.ProjectDao
import com.example.core.database.dao.SceneDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fikra_video.db"
        )
            .fallbackToDestructiveMigration()
            .addMigrations(
                com.example.core.database.MIGRATION_1_2,
                com.example.core.database.MIGRATION_2_3,
                com.example.core.database.MIGRATION_3_4
            )
            .build()
    }

    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao = database.projectDao()

    @Provides
    fun provideSceneDao(database: AppDatabase): SceneDao = database.sceneDao()

    @Provides
    fun provideAssetDao(database: AppDatabase): AssetDao = database.assetDao()

    @Provides
    fun provideJobDao(database: AppDatabase): JobDao = database.jobDao()

    @Provides
    fun provideIdeaDao(database: AppDatabase): IdeaDao = database.ideaDao()
}
