package com.example.jejaku.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object for Location operations.
 * Provides methods to interact with the locations table.
 */
@Dao
interface LocationDao {

    /**
     * Insert a new location record.
     * @param location The location entity to insert
     */
    @Insert
    suspend fun insert(location: LocationEntity)

    /**
     * Get all location records, ordered by timestamp descending (newest first).
     * @return LiveData list of all locations
     */
    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<LocationEntity>>

    /**
     * Delete a location record by its ID.
     * @param id The ID of the location to delete
     */
    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Get a single location by ID.
     * @param id The ID of the location to retrieve
     * @return The location entity or null if not found
     */
    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LocationEntity?
}
