package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(

    @PrimaryKey
    val currencyCode: String,

    val rateToNZD: Double,

    @Serializable(with = LocalDateSerializer::class)
    val lastUpdated: LocalDate
)