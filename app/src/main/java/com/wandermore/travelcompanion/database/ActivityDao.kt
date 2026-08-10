package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query(
        """
        SELECT * FROM activities
        WHERE tripId = :tripId
        ORDER BY
            CASE WHEN date IS NULL THEN 1 ELSE 0 END,
            date ASC,
            CASE WHEN startTime IS NULL THEN 1 ELSE 0 END,
            startTime ASC,
            booked ASC,
            id ASC
        """
    )
    fun getActivitiesForTrip(
        tripId: Long
    ): Flow<List<ActivityEntity>>

    // ---------------------------------------------------------
    // BACKUP
    // ---------------------------------------------------------

    @Query("SELECT * FROM activities")
    suspend fun getAllActivitiesForBackup(): List<ActivityEntity>

    @Insert
    suspend fun insertActivity(
        activity: ActivityEntity
    ): Long

    @Update
    suspend fun updateActivity(
        activity: ActivityEntity
    )

    @Delete
    suspend fun deleteActivity(
        activity: ActivityEntity
    )

    @Query(
        "SELECT * FROM activities WHERE id = :activityId LIMIT 1"
    )
    suspend fun getActivityById(
        activityId: Long
    ): ActivityEntity?
}