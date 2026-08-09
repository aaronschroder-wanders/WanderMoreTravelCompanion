package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trip_estimates",

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
        Index(
            value = ["tripId", "category"],
            unique = true
        )
    ]
)
data class TripEstimateEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    val category: String,

    val estimateType: String,

    val amount: Double,

    val currency: String,

    val convertedAmount: Double,

    val notes: String? = null
)