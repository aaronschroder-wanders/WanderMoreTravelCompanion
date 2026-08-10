package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(
    tableName = "expenses",
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
data class ExpenseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tripId: Long,

    val description: String,

    val amount: Double,

    val currency: String,

    val category: String,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    // Exchange rate used when this expense was entered
    val exchangeRate: Double = 1.0,

    // Converted value stored for historical accuracy
    val convertedAmount: Double = 0.0,

    // Number of nights for accommodation expenses
    val numberOfNights: Int? = null
)