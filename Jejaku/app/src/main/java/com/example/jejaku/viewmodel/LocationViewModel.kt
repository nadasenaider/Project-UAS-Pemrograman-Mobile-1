package com.example.jejaku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.jejaku.data.AppDatabase
import com.example.jejaku.data.LocationEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for managing location data.
 * Handles all database operations and exposes LiveData for UI observation.
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationDao = AppDatabase.getDatabase(application).locationDao()
    val allLocations: LiveData<List<LocationEntity>> = locationDao.getAll()

    /**
     * Insert a new location record.
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param locationName The name of the location
     * @param date The date string
     * @param note The user's note for this location
     */
    fun insertLocation(latitude: Double, longitude: Double, locationName: String, date: String, note: String) {
        viewModelScope.launch {
            val location = LocationEntity(
                latitude = latitude,
                longitude = longitude,
                locationName = locationName,
                date = date,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            locationDao.insert(location)
        }
    }

    /**
     * Delete a location record by its ID.
     * @param id The ID of the location to delete
     */
    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            locationDao.deleteById(id)
        }
    }
}
