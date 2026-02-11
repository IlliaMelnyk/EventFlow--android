package com.example.eventflow.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: Events)

    @Query("SELECT * FROM events")
    fun getAll(): Flow<List<Events>>

    @Update
    suspend fun update(events: Events)

    @Delete
    suspend fun delete(events: Events)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: String): Events
}
