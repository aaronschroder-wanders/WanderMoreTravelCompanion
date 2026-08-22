package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "trip_destinations",
    primaryKeys = ["tripId", "destinationId"],
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
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
        Index(value = ["tripId"]),
        Index(value = ["destinationId"])
    ]
)
data class TripDestinationEntity(

    val tripId: Long,

    val destinationId: Long
)