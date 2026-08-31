package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
    // GET ITINERARY ITEMS FOR A SPECIFIC DATE
    // ---------------------------------------------------------

    @Query(
        """
    SELECT *
    FROM itinerary
    WHERE tripId = :tripId
      AND date = :date
    ORDER BY
        CASE WHEN time IS NULL THEN 1 ELSE 0 END,
        time ASC,
        sortOrder ASC
    """
    )
    fun getItineraryForDate(
        tripId: Long,
        date: LocalDate
    ): Flow<List<ItineraryEntity>>

    // ---------------------------------------------------------
    // BACKUP
    // ---------------------------------------------------------

    @Query("SELECT * FROM itinerary")
    suspend fun getAllItineraryForBackup(): List<ItineraryEntity>

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
    // GET ITINERARY ITEM LINKED TO AN ACTIVITY
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM itinerary
        WHERE activityId = :activityId
        LIMIT 1
        """
    )
    suspend fun getItineraryByActivityId(
        activityId: Long
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
    // DELETE BY ACTIVITY
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM itinerary
        WHERE activityId = :activityId
        """
    )
    suspend fun deleteItineraryByActivityId(
        activityId: Long
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