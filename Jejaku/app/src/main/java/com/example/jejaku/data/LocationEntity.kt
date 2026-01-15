package com.example.jejaku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a location record.
 * Stores GPS coordinates, location name, date, and notes.
 */
@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val latitude: Double,

    val longitude: Double,

    val locationName: String,

    val date: String,

    val note: String,

    val timestamp: Long = System.currentTimeMillis()
)
