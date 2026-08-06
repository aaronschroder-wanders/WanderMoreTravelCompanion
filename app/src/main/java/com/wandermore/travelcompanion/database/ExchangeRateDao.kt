package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllRates(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :currency LIMIT 1")
    suspend fun getRate(currency: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(
        rates: List<ExchangeRateEntity>
    )

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAllRates()

}