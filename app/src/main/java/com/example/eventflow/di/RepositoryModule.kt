package com.example.eventflow.di


import com.example.eventflow.database.EventsDao
import com.example.eventflow.database.EventsLocalRepositoryImpl
import com.example.eventflow.database.IEventsLocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(dao: EventsDao) : IEventsLocalRepository {
        return EventsLocalRepositoryImpl(dao)
    }
}