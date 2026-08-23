package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDestinationDao {

    // ---------------------------------------------------------
    // GET DESTINATIONS FOR AN ACTIVITY
    // ---------------------------------------------------------

    @Query(
        """
        SELECT d.*
        FROM destinations d
        INNER JOIN activity_destinations ad
            ON d.id = ad.destinationId
        WHERE ad.activityId = :activityId
        ORDER BY d.name COLLATE NOCASE ASC
        """
    )
    suspend fun getDestinationsForActivity(
        activityId: Long
    ): List<DestinationEntity>


    // ---------------------------------------------------------
    // GET DESTINATION IDS FOR AN ACTIVITY
    // ---------------------------------------------------------

    @Query(
        """
        SELECT destinationId
        FROM activity_destinations
        WHERE activityId = :activityId
        """
    )
    suspend fun getDestinationIdsForActivity(
        activityId: Long
    ): List<Long>

    // ---------------------------------------------------------
    // GET ACTIVITY IDS FOR A DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        SELECT activityId
        FROM activity_destinations
        WHERE destinationId = :destinationId
        """
    )
    suspend fun getActivityIdsForDestination(
        destinationId: Long
    ): List<Long>

    // ---------------------------------------------------------
    // COUNT ACTIVITIES USING A DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        SELECT COUNT(*)
        FROM activity_destinations
        WHERE destinationId = :destinationId
        """
    )
    suspend fun countActivitiesForDestination(
        destinationId: Long
    ): Int


    // ---------------------------------------------------------
    // ADD DESTINATION
    // ---------------------------------------------------------

    @Insert
    suspend fun insertActivityDestination(
        activityDestination: ActivityDestinationEntity
    )


    // ---------------------------------------------------------
    // REMOVE DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM activity_destinations
        WHERE activityId = :activityId
        AND destinationId = :destinationId
        """
    )
    suspend fun deleteActivityDestination(
        activityId: Long,
        destinationId: Long
    )


    // ---------------------------------------------------------
    // REMOVE ALL DESTINATIONS
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM activity_destinations
        WHERE activityId = :activityId
        """
    )
    suspend fun deleteDestinationsForActivity(
        activityId: Long
    )
}