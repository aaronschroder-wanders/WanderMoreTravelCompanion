package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "itinerary_destinations",
    primaryKeys = ["itineraryId", "destinationId"],
    foreignKeys = [
        ForeignKey(
            entity = ItineraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["itineraryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DestinationEntity::class,
            parentColumns = ["id"],
            childColumns = ["destinationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["itineraryId"]),
        Index(value = ["destinationId"])
    ]
)
data class ItineraryDestinationEntity(

    val itineraryId: Long,

    val destinationId: Long
)