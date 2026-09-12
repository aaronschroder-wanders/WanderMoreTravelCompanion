package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryLinkDao {

    @Query(
        """
        SELECT * FROM itinerary_links
        WHERE itineraryId = :itineraryId
        ORDER BY id
        """
    )
    fun getLinksForItinerary(itineraryId: Long): Flow<List<ItineraryLinkEntity>>

    @Insert
    suspend fun insert(link: ItineraryLinkEntity): Long

    @Update
    suspend fun update(link: ItineraryLinkEntity)

    @Delete
    suspend fun delete(link: ItineraryLinkEntity)

    @Query("DELETE FROM itinerary_links WHERE itineraryId = :itineraryId")
    suspend fun deleteLinksForItinerary(itineraryId: Long)
}