package com.example.eventflow.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Events(
    @PrimaryKey val id: String, // Firebase ID
    val name: String,
    val category: String,
    val description: String,
    val date: Long?,
    val imageUrl: String? = null,
    var ownerId: String? = null,
    @ColumnInfo(name = "latitude")
    var latitude: Double? = null,
    @ColumnInfo(name = "longitude")
    var longitude: Double? = null)
{

    fun hasLocation(): Boolean = latitude != null && longitude != null
}