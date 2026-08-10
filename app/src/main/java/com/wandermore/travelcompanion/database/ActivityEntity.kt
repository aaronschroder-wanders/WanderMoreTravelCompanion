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
    tableName = "activities",
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
data class ActivityEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    val name: String,

    val type: String,

    val location: String? = null,

    val estimatedCost: Double? = null,

    val currency: String? = null,

    val convertedAmount: Double? = null,

    val booked: Boolean = false,

    val website: String? = null,

    val notes: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val startTime: LocalTime? = null
)