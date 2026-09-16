package com.example.core.di

import com.example.data.repository.IdeaRepository
import com.example.data.repository.IdeaRepositoryImpl
import com.example.data.repository.ProjectCleanupRepository
import com.example.data.repository.ProjectCleanupRepositoryImpl
import com.example.data.repository.ProjectRepository
import com.example.data.repository.ProjectRepositoryImpl
import com.example.data.repository.SceneRepository
import com.example.data.repository.SceneRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        implementation: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(
        implementation: SceneRepositoryImpl
    ): SceneRepository

    @Binds
    @Singleton
    abstract fun bindProjectCleanupRepository(
        implementation: ProjectCleanupRepositoryImpl
    ): ProjectCleanupRepository

    @Binds
    @Singleton
    abstract fun bindIdeaRepository(
        implementation: IdeaRepositoryImpl
    ): IdeaRepository
}
