package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItineraryDestinationDao {

    // ---------------------------------------------------------
    // GET DESTINATIONS FOR AN ITINERARY ITEM
    // ---------------------------------------------------------

    @Query(
        """
        SELECT d.*
        FROM destinations d
        INNER JOIN itinerary_destinations id
            ON d.id = id.destinationId
        WHERE id.itineraryId = :itineraryId
        ORDER BY d.name COLLATE NOCASE ASC
        """
    )
    suspend fun getDestinationsForItinerary(
        itineraryId: Long
    ): List<DestinationEntity>


    // ---------------------------------------------------------
    // GET DESTINATION IDS FOR AN ITINERARY ITEM
    // ---------------------------------------------------------

    @Query(
        """
        SELECT destinationId
        FROM itinerary_destinations
        WHERE itineraryId = :itineraryId
        """
    )
    suspend fun getDestinationIdsForItinerary(
        itineraryId: Long
    ): List<Long>

    // ---------------------------------------------------------
    // GET ITINERARY IDS FOR A DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        SELECT itineraryId
        FROM itinerary_destinations
        WHERE destinationId = :destinationId
        """
    )
    suspend fun getItineraryIdsForDestination(
        destinationId: Long
    ): List<Long>

    // ---------------------------------------------------------
    // COUNT ITINERARIES USING A DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        SELECT COUNT(*)
        FROM itinerary_destinations
        WHERE destinationId = :destinationId
        """
    )
    suspend fun countItinerariesForDestination(
        destinationId: Long
    ): Int


    // ---------------------------------------------------------
    // ADD DESTINATION
    // ---------------------------------------------------------

    @Insert
    suspend fun insertItineraryDestination(
        itineraryDestination: ItineraryDestinationEntity
    )


    // ---------------------------------------------------------
    // REMOVE DESTINATION
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM itinerary_destinations
        WHERE itineraryId = :itineraryId
        AND destinationId = :destinationId
        """
    )
    suspend fun deleteItineraryDestination(
        itineraryId: Long,
        destinationId: Long
    )


    // ---------------------------------------------------------
    // REMOVE ALL DESTINATIONS
    // ---------------------------------------------------------

    @Query(
        """
        DELETE FROM itinerary_destinations
        WHERE itineraryId = :itineraryId
        """
    )
    suspend fun deleteDestinationsForItinerary(
        itineraryId: Long
    )
}