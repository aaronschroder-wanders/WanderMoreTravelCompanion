package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripEstimateDao {

// ---------------------------------------------------------
// GET ALL ESTIMATES FOR A TRIP
// ---------------------------------------------------------

    @Query(
        """
    SELECT *
    FROM trip_estimates
    WHERE tripId = :tripId
    ORDER BY category
    """
    )
    fun getEstimatesForTrip(
        tripId: Long
    ): Flow<List<TripEstimateEntity>>


// ---------------------------------------------------------
// GET ESTIMATE FOR A TRIP + CATEGORY
// ---------------------------------------------------------

    @Query(
        """
    SELECT *
    FROM trip_estimates
    WHERE tripId = :tripId
    AND category = :category
    LIMIT 1
    """
    )
    suspend fun getEstimate(
        tripId: Long,
        category: String
    ): TripEstimateEntity?


// ---------------------------------------------------------
// GET ESTIMATE BY ID
// ---------------------------------------------------------

    @Query(
        """
    SELECT *
    FROM trip_estimates
    WHERE id = :estimateId
    LIMIT 1
    """
    )
    suspend fun getEstimateById(
        estimateId: Long
    ): TripEstimateEntity?


// ---------------------------------------------------------
// INSERT
// ---------------------------------------------------------

    @Insert
    suspend fun insertEstimate(
        estimate: TripEstimateEntity
    ): Long


// ---------------------------------------------------------
// UPDATE
// ---------------------------------------------------------

    @Update
    suspend fun updateEstimate(
        estimate: TripEstimateEntity
    )


// ---------------------------------------------------------
// DELETE
// ---------------------------------------------------------

    @Delete
    suspend fun deleteEstimate(
        estimate: TripEstimateEntity
    )


// ---------------------------------------------------------
// DELETE BY TRIP
// Useful if we ever need to remove all estimates
// for a particular trip.
// ---------------------------------------------------------

    @Query(
        """
    DELETE FROM trip_estimates
    WHERE tripId = :tripId
    """
    )
    suspend fun deleteEstimatesForTrip(
        tripId: Long
    )

}
