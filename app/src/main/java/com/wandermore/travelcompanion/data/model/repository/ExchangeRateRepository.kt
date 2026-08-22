package com.wandermore.travelcompanion.data.repository

import com.wandermore.travelcompanion.data.service.CurrencyRateService
import com.wandermore.travelcompanion.database.ExchangeRateDao
import com.wandermore.travelcompanion.database.ExchangeRateEntity
import java.time.LocalDate

class ExchangeRateRepository(

    private val exchangeRateDao: ExchangeRateDao,

    private val currencyRateService: CurrencyRateService

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


    /**
     * Retrieves current exchange rates from the currency service
     * and stores them in Room.
     *
     * Frankfurter uses NZD as the base currency and returns rates
     * in the form:
     *
     *     1 NZD = X foreign currency
     *
     * The application database stores rates as:
     *
     *     1 foreign currency = X NZD
     *
     * Therefore each service rate is inverted before being stored.
     *
     * Example:
     *
     *     API:
     *     1 NZD = 1.1969 AUD
     *
     *     Database:
     *     1 AUD = 0.8355 NZD
     *
     * If the service cannot be reached, existing Room rates are
     * retained so the application continues to work offline.
     */
    suspend fun refreshRates(): Boolean {

        return try {

            val serviceRates =
                currencyRateService.getRates()


            println(
                "CURRENCY API: received " +
                        "${serviceRates.size} rates"
            )


            serviceRates.forEach {

                println(
                    "CURRENCY API: " +
                            "${it.base} -> ${it.quote} = " +
                            "${it.rate}, " +
                            "date = ${it.date}"
                )

            }


            val updatedRates =
                mutableListOf<ExchangeRateEntity>()


            /*
             * Use the date supplied by the API rather than
             * LocalDate.now().
             *
             * This is important because Frankfurter supplies
             * reference rates for a particular business date.
             */
            val apiDate =
                serviceRates
                    .firstOrNull()
                    ?.date
                    ?.let {

                        LocalDate.parse(it)

                    }
                    ?: LocalDate.now()


            /*
             * NZD is our underlying reference currency.
             */
            updatedRates.add(

                ExchangeRateEntity(

                    currencyCode = "NZD",

                    rateToNZD = 1.0,

                    lastUpdated = apiDate

                )

            )


            /*
             * Convert the API's:
             *
             *     1 NZD = X foreign currency
             *
             * into our database representation:
             *
             *     1 foreign currency = X NZD
             *
             * by calculating:
             *
             *     1 / API rate
             */
            serviceRates.forEach { serviceRate ->

                if (
                    serviceRate.base == "NZD" &&
                    serviceRate.rate > 0.0
                ) {

                    val rateToNZD =
                        1.0 / serviceRate.rate


                    updatedRates.add(

                        ExchangeRateEntity(

                            currencyCode =
                                serviceRate.quote,

                            rateToNZD =
                                rateToNZD,

                            lastUpdated =
                                LocalDate.parse(
                                    serviceRate.date
                                )

                        )

                    )

                }

            }


            /*
             * Only replace the database contents if we received
             * valid currency data.
             */
            if (updatedRates.size > 1) {

                exchangeRateDao.insertRates(
                    updatedRates
                )


                println(
                    "CURRENCY DATABASE: saved " +
                            "${updatedRates.size} rates"
                )


                /*
                 * Diagnostic output so we can verify exactly
                 * what was written to Room.
                 */
                updatedRates.forEach {

                    println(
                        "CURRENCY DATABASE: " +
                                "${it.currencyCode} = " +
                                "${it.rateToNZD}, " +
                                "updated = " +
                                "${it.lastUpdated}"
                    )

                }


                true

            } else {

                println(
                    "CURRENCY DATABASE: " +
                            "no valid rates received"
                )


                false

            }

        } catch (
            exception: Exception
        ) {

            println(
                "CURRENCY API ERROR: " +
                        "${exception.javaClass.simpleName}: " +
                        "${exception.message}"
            )


            false

        }

    }


    /**
     * Initialises exchange rates when the application starts.
     *
     * If the database is empty, current rates are retrieved from
     * the currency service.
     *
     * If rates already exist, they are retained so that the app
     * can start without requiring an internet connection.
     */
    suspend fun insertInitialRates(): Boolean {

        val existingRates =
            exchangeRateDao.getAllRates()


        if (existingRates.isEmpty()) {

            return refreshRates()

        }


        return true

    }

}