package com.wandermore.travelcompanion.data.repository

import com.wandermore.travelcompanion.database.ExchangeRateDao
import com.wandermore.travelcompanion.database.ExchangeRateEntity
import java.time.LocalDate

class ExchangeRateRepository(

    private val exchangeRateDao: ExchangeRateDao

) {


    suspend fun getRate(
        currency: String
    ): Double? {

        return exchangeRateDao
            .getRate(currency)
            ?.rateToNZD

    }



    suspend fun getAllRates(): List<ExchangeRateEntity> {

        return exchangeRateDao.getAllRates()

    }



    suspend fun updateRate(
        currencyCode: String,
        newRate: Double
    ) {

        val existingRate =
            exchangeRateDao.getRate(currencyCode)


        if (existingRate != null) {


            exchangeRateDao.insertRates(

                listOf(

                    existingRate.copy(

                        rateToNZD = newRate,

                        lastUpdated = LocalDate.now()

                    )

                )

            )

        }

    }



    suspend fun insertInitialRates() {


        val existingRates =
            exchangeRateDao.getAllRates()


        if (existingRates.isNotEmpty()) {

            return

        }


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
            ),

            ExchangeRateEntity(
                "JPY",
                0.011,
                today
            ),

            ExchangeRateEntity(
                "KRW",
                0.0012,
                today
            ),

            ExchangeRateEntity(
                "MYR",
                0.39,
                today
            ),

            ExchangeRateEntity(
                "SGD",
                1.32,
                today
            ),

            ExchangeRateEntity(
                "IDR",
                0.00010,
                today
            ),

            ExchangeRateEntity(
                "PHP",
                0.030,
                today
            ),

            ExchangeRateEntity(
                "INR",
                0.019,
                today
            )

        )


        exchangeRateDao.insertRates(
            rates
        )

    }

}