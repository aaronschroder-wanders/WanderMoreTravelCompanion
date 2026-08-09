package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

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
        Index(value = ["tripId"])
    ]
)
data class ItineraryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    val date: LocalDate,

    val time: LocalTime? = null,

    val title: String,

    val type: String,

    val nights: Int? = null,

    val location: String? = null,

    val notes: String? = null,

    val bookingId: Long? = null,

    val sortOrder: Int = 0
)