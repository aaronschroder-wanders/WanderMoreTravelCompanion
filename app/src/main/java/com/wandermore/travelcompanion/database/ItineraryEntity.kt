package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
@Entity(
    tableName = "itinerary",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tripId"]),
        Index(value = ["activityId"])
    ]
)
data class ItineraryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @Serializable(with = LocalTimeSerializer::class)
    val time: LocalTime? = null,

    val title: String,

    val type: String,

    val nights: Int? = null,

    val location: String? = null,

    val notes: String? = null,

    // Reserved for a future proper Booking relationship.
    val bookingId: Long? = null,

    // Links this itinerary item back to its Activity.
    // Null means it was created manually or from another source.
    val activityId: Long? = null,

    // Whether this itinerary item has been booked.
    val booked: Boolean = false,

    val sortOrder: Int = 0
)