package com.wandermore.travelcompanion.util

object ExchangeRates {

    private val ratesToNZD = mapOf(

        "NZD" to 1.0,

        "AUD" to 0.92,

        "USD" to 1.70,

        "EUR" to 1.95,

        "GBP" to 2.25,

        "THB" to 0.048,

        "VND" to 0.000056,

        "LAK" to 0.000080,

        "CNY" to 0.24

    )


    fun getRate(
        currency: String
    ): Double {

        return ratesToNZD[currency]
            ?: 1.0

    }

}