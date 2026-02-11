package com.example.eventflow.database
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventsLocalRepositoryImpl @Inject constructor(private val eventsDao : EventsDao) : IEventsLocalRepository {

    override suspend fun insert(event: Events) {
        return eventsDao.insert(event)
    }

    override fun getAll(): Flow<List<Events>> {
        return  eventsDao.getAll()
    }

    override suspend fun update(event: Events) {
        eventsDao.update(event)
    }

    override suspend fun delete(event: Events) {
        eventsDao.delete(event)
    }

    override suspend fun getById(id: String): Events = eventsDao.getById(id)

}