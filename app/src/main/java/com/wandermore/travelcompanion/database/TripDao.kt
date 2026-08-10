package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // =========================================================
    // NORMAL TRIP LIST
    // =========================================================

    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<TripEntity>>


    // =========================================================
    // GET SINGLE TRIP
    // =========================================================

    @Query(
        """
        SELECT * FROM trips
        WHERE id = :tripId
        """
    )
    fun getTripById(
        tripId: Long
    ): Flow<TripEntity?>


    // =========================================================
    // BACKUP
    // =========================================================

    @Query("SELECT * FROM trips")
    suspend fun getAllTripsForBackup(): List<TripEntity>


    // =========================================================
    // RESTORE
    // Delete all trips before restoring backup
    //
    // Child records are deleted automatically through
    // the ON DELETE CASCADE relationships.
    // =========================================================

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()


    // =========================================================
    // NORMAL TRIP OPERATIONS
    // =========================================================

    @Insert
    suspend fun insertTrip(
        trip: TripEntity
    )


    @Update
    suspend fun updateTrip(
        trip: TripEntity
    )


    // =========================================================
    // TRIP STATUS
    // =========================================================

    @Query(
        """
        UPDATE trips
        SET status = :status
        WHERE id = :tripId
        """
    )
    suspend fun updateTripStatus(
        tripId: Long,
        status: String
    )


    // =========================================================
    // DELETE SINGLE TRIP
    // =========================================================

    @Delete
    suspend fun deleteTrip(
        trip: TripEntity
    )
}
