package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(
    tableName = "trips"
)
data class TripEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Long = 0,

    val name: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val homeCurrency: String

)