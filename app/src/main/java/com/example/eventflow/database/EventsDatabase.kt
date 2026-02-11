package com.example.eventflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Events::class], version = 4, exportSchema = true)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventsDao(): EventsDao

    companion object {
        private var INSTANCE: EventsDatabase? = null
        fun getDatabase(context: Context): EventsDatabase {
            if (INSTANCE == null) {
                synchronized(EventsDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            EventsDatabase::class.java,
                            "events_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }

    }
}
