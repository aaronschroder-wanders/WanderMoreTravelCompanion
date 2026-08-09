package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {

    // ---------------------------------------------------------
    // GET ALL ITINERARY ITEMS FOR A TRIP
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM itinerary
        WHERE tripId = :tripId
        ORDER BY date ASC, sortOrder ASC, time ASC
        """
    )
    fun getItineraryForTrip(
        tripId: Long
    ): Flow<List<ItineraryEntity>>


    // ---------------------------------------------------------
    // GET ONE ITINERARY ITEM
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM itinerary
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getItineraryById(
        id: Long
    ): ItineraryEntity?


    // ---------------------------------------------------------
    // ADD
    // ---------------------------------------------------------

    @Insert
    suspend fun insertItinerary(
        itinerary: ItineraryEntity
    ): Long


    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    @Update
    suspend fun updateItinerary(
        itinerary: ItineraryEntity
    )


    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    @Delete
    suspend fun deleteItinerary(
        itinerary: ItineraryEntity
    )


    // ---------------------------------------------------------
    // DELETE ALL FOR A TRIP
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM itinerary
        WHERE tripId = :tripId
        """
    )
    suspend fun deleteItineraryForTrip(
        tripId: Long
    )
}