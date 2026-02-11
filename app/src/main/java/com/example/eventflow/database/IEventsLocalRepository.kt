package com.example.eventflow.database

import kotlinx.coroutines.flow.Flow

interface IEventsLocalRepository {
    suspend fun insert(event: Events)
    fun getAll(): Flow<List<Events>>
    suspend fun update(event: Events)
    suspend fun delete(event: Events)
    suspend fun getById(id: String): Events

}