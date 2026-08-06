package com.wandermore.travelcompanion.data.repository

import com.wandermore.travelcompanion.database.ExchangeRateDao
import com.wandermore.travelcompanion.database.ExchangeRateEntity
import java.time.LocalDate


class ExchangeRateRepository(

    private val exchangeRateDao: ExchangeRateDao

) {


    suspend fun getRate(
        currency: String
    ): Double {

        return exchangeRateDao
            .getRate(currency)
            ?.rateToNZD
            ?: 1.0

    }

    suspend fun getAllRates(): List<ExchangeRateEntity> {

        return exchangeRateDao.getAllRates()

    }

    suspend fun insertInitialRates() {


        val today =
            LocalDate.now()



        val rates = listOf(

            ExchangeRateEntity(
                "NZD",
                1.0,
                today
            ),

            ExchangeRateEntity(
                "AUD",
                0.92,
                today
            ),

            ExchangeRateEntity(
                "USD",
                1.70,
                today
            ),

            ExchangeRateEntity(
                "EUR",
                1.95,
                today
            ),

            ExchangeRateEntity(
                "GBP",
                2.25,
                today
            ),

            ExchangeRateEntity(
                "THB",
                0.048,
                today
            ),

            ExchangeRateEntity(
                "VND",
                0.000056,
                today
            ),

            ExchangeRateEntity(
                "LAK",
                0.000080,
                today
            ),

            ExchangeRateEntity(
                "CNY",
                0.24,
                today
            )

        )


        exchangeRateDao.insertRates(
            rates
        )

    }

}