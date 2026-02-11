package com.example.eventflow.firestore

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val date: Long? = null,
    val ownerId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String = ""
){
    fun hasLocation(): Boolean = latitude != null && longitude != null
}