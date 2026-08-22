package com.wandermore.travelcompanion.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class CurrencyRateResponse(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)

class CurrencyRateService {

    companion object {

        private const val BASE_URL =
            "https://api.frankfurter.dev/v2/rates"

        private const val BASE_CURRENCY =
            "NZD"

        private const val QUOTES =
            "AUD,USD,EUR,GBP,THB,VND,LAK,CNY,JPY,KRW,MYR,SGD,IDR,PHP,INR,CAD,CHF,AED,TRY,ZAR,NOK,SEK,DKK"
    }


    private val json =
        Json {
            ignoreUnknownKeys = true
        }


    suspend fun getRates(): List<CurrencyRateResponse> =
        withContext(Dispatchers.IO) {

            val url =
                URL(
                    "$BASE_URL" +
                            "?base=$BASE_CURRENCY" +
                            "&quotes=$QUOTES"
                )


            val connection =
                url.openConnection() as HttpURLConnection


            try {

                connection.requestMethod = "GET"

                connection.connectTimeout = 10_000

                connection.readTimeout = 10_000

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )


                val responseCode =
                    connection.responseCode


                if (responseCode !in 200..299) {

                    throw Exception(
                        "Currency service returned HTTP $responseCode"
                    )

                }


                val responseBody =
                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }


                json.decodeFromString(
                    responseBody
                )

            } finally {

                connection.disconnect()

            }

        }

}