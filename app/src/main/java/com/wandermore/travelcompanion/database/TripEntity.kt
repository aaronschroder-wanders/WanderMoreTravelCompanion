package com.wandermore.travelcompanion.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wandermore.travelcompanion.model.TripStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(
    tableName = "trips"
)
data class TripEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Long = 0,

    val name: String,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,

    val homeCurrency: String,

    @ColumnInfo(name = "status")
    val status: String = TripStatus.PLANNED.name
)