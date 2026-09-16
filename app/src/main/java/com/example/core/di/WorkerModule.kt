package com.example.core.di

import com.example.data.repository.MediaGenerationRepository
import com.example.data.repository.MediaGenerationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {
    @Binds
    @Singleton
    abstract fun bindMediaGenerationRepository(
        implementation: MediaGenerationRepositoryImpl
    ): MediaGenerationRepository
}
