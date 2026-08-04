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


    @Insert
    suspend fun insertTrip(
        trip: TripEntity
    )


    @Update
    suspend fun updateTrip(
        trip: TripEntity
    )


    @Delete
    suspend fun deleteTrip(
        trip: TripEntity
    )

}