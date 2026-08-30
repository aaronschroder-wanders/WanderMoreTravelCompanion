package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {

    // ---------------------------------------------------------
    // GET ALL ACTIVE DESTINATIONS
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM destinations
        WHERE active = 1
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun getActiveDestinations(): Flow<List<DestinationEntity>>

    // ---------------------------------------------------------
    // GET ALL DESTINATIONS
    //
    // Includes archived destinations.
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM destinations
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun getAllDestinations(): Flow<List<DestinationEntity>>

    // ---------------------------------------------------------
    // GET ONE DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM destinations
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getDestinationById(
        id: Long
    ): DestinationEntity?

    // ---------------------------------------------------------
    // FIND BY NAME
    // ---------------------------------------------------------

    @Query(
        """
        SELECT *
        FROM destinations
        WHERE name = :name
        COLLATE NOCASE
        LIMIT 1
        """
    )
    suspend fun getDestinationByName(
        name: String
    ): DestinationEntity?

    // ---------------------------------------------------------
    // ADD
    // ---------------------------------------------------------

    @Insert
    suspend fun insertDestination(
        destination: DestinationEntity
    ): Long

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    @Update
    suspend fun updateDestination(
        destination: DestinationEntity
    )

    // ---------------------------------------------------------
    // SET ACTIVE / ARCHIVED
    // ---------------------------------------------------------

    @Query(
        """
        UPDATE destinations
        SET active = :active
        WHERE id = :destinationId
        """
    )
    suspend fun setDestinationActive(
        destinationId: Long,
        active: Boolean
    )

    // ---------------------------------------------------------
    // DELETE
    //
    // Used only after confirming that the destination has
    // no remaining references anywhere in the database.
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM destinations
        WHERE id = :destinationId
        """
    )
    suspend fun deleteDestination(
        destinationId: Long
    )

    // ---------------------------------------------------------
// DELETE ALL DESTINATIONS
//
// Used when restoring a complete database backup.
// ---------------------------------------------------------

    @Query(
        """
    DELETE FROM destinations
    """
    )
    suspend fun deleteAllDestinations()

}