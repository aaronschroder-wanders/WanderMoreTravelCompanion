package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(

    @PrimaryKey
    val currencyCode: String,

    val rateToNZD: Double,

    val lastUpdated: LocalDate

)