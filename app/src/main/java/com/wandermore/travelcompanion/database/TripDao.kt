package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TripDao {


    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<TripEntity>>


    @Query(
        """
        SELECT * FROM trips
        WHERE id = :tripId
        """
    )
    fun getTripById(
        tripId: Long
    ): Flow<TripEntity?>


    @Insert
    suspend fun insertTrip(
        trip: TripEntity
    )


    @Update
    suspend fun updateTrip(
        trip: TripEntity
    )


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


    @Delete
    suspend fun deleteTrip(
        trip: TripEntity
    )

}