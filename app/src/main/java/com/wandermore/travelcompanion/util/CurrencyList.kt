package com.wandermore.travelcompanion.util

data class CurrencyItem(
    val code: String,
    val name: String
)

val supportedCurrencies = listOf(

    CurrencyItem(
        "NZD",
        "New Zealand Dollar"
    ),

    CurrencyItem(
        "AUD",
        "Australian Dollar"
    ),

    CurrencyItem(
        "USD",
        "US Dollar"
    ),

    CurrencyItem(
        "EUR",
        "Euro"
    ),

    CurrencyItem(
        "GBP",
        "British Pound"
    ),

    CurrencyItem(
        "THB",
        "Thai Baht"
    ),

    CurrencyItem(
        "VND",
        "Vietnamese Dong"
    ),

    CurrencyItem(
        "LAK",
        "Lao Kip"
    ),

    CurrencyItem(
        "CNY",
        "Chinese Yuan"
    )

)