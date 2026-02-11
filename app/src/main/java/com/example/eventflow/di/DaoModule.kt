package com.example.eventflow.di

import com.example.eventflow.database.EventsDao
import com.example.eventflow.database.EventsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideDao(database: EventsDatabase) : EventsDao {
        return database.eventsDao()
    }
}