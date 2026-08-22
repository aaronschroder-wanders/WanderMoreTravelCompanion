package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDestinationDao {

    // ---------------------------------------------------------
    // GET DESTINATIONS FOR A TRIP
    // ---------------------------------------------------------

    @Query(
        """
        SELECT d.*
        FROM destinations d
        INNER JOIN trip_destinations td
            ON d.id = td.destinationId
        WHERE td.tripId = :tripId
        ORDER BY d.name COLLATE NOCASE ASC
        """
    )
    fun getDestinationsForTrip(
        tripId: Long
    ): Flow<List<DestinationEntity>>

    // ---------------------------------------------------------
    // GET DESTINATION IDS FOR A TRIP
    // ---------------------------------------------------------

    @Query(
        """
        SELECT destinationId
        FROM trip_destinations
        WHERE tripId = :tripId
        """
    )
    suspend fun getDestinationIdsForTrip(
        tripId: Long
    ): List<Long>

    // ---------------------------------------------------------
    // ADD DESTINATION TO TRIP
    // ---------------------------------------------------------

    @Insert
    suspend fun insertTripDestination(
        tripDestination: TripDestinationEntity
    )

    // ---------------------------------------------------------
    // REMOVE DESTINATION FROM TRIP
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM trip_destinations
        WHERE tripId = :tripId
        AND destinationId = :destinationId
        """
    )
    suspend fun deleteTripDestination(
        tripId: Long,
        destinationId: Long
    )

    // ---------------------------------------------------------
    // REMOVE ALL DESTINATIONS FROM TRIP
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM trip_destinations
        WHERE tripId = :tripId
        """
    )
    suspend fun deleteDestinationsForTrip(
        tripId: Long
    )
}