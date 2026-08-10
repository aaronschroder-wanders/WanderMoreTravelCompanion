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
    tableName = "bookings",
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
data class BookingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    val name: String,

    val type: String,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val time: LocalTime? = null,

    val provider: String? = null,

    val reference: String? = null,

    val cost: Double? = null,

    val currency: String? = null,

    val convertedAmount: Double? = null,

    @Serializable(with = LocalDateSerializer::class)
    val cancelFreeBefore: LocalDate? = null,

    val address: String? = null,

    val website: String? = null,

    val notes: String? = null,

    val status: String = "PLANNED"
)