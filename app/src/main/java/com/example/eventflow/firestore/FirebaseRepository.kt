package com.example.eventflow.firestore

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(){

    private val db = FirebaseFirestore.getInstance()
    private val eventsCollection = db.collection("events")

    suspend fun addEvent(event: Event) {
        try {
            val newDocRef = eventsCollection.document()
            val eventWithId = event.copy(id = newDocRef.id)
            newDocRef.set(eventWithId).await()
            Log.d("FirebaseRepository", "Event added: ${eventWithId.id}")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error adding event", e)
        }
    }

    suspend fun updateEvent(event: Event) {
        try {
            val id = event.id
            if (!id.isNullOrBlank()) {
                eventsCollection.document(id).set(event).await()
                Log.d("FirebaseRepository", "Event updated: $id")
            } else {
                Log.e("FirebaseRepository", "Cannot update event: ID is null or blank")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating event", e)
        }
    }

    suspend fun getEvents(): List<Event> {
        return try {
            val snapshot = eventsCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject<Event>() }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting events", e)
            emptyList()
        }
    }

    suspend fun deleteEvent(id: String) {
        try {
            eventsCollection.document(id).delete().await()
            Log.d("FirebaseRepository", "Event deleted: $id")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error deleting event", e)
        }
    }

    suspend fun getEventById(id: String): Event? {
        return try {
            val snapshot = eventsCollection.document(id).get().await()
            snapshot.toObject(Event::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting event by ID", e)
            null
        }
    }
}
