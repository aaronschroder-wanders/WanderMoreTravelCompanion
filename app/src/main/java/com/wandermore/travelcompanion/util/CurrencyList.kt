package com.wandermore.travelcompanion.util


data class CurrencyItem(
    val code: String,
    val name: String,
    val symbol: String
)


val supportedCurrencies = listOf(

    CurrencyItem(
        "NZD",
        "New Zealand Dollar",
        "$"
    ),

    CurrencyItem(
        "AUD",
        "Australian Dollar",
        "$"
    ),

    CurrencyItem(
        "USD",
        "US Dollar",
        "$"
    ),

    CurrencyItem(
        "EUR",
        "Euro",
        "€"
    ),

    CurrencyItem(
        "GBP",
        "British Pound",
        "£"
    ),

    CurrencyItem(
        "THB",
        "Thai Baht",
        "฿"
    ),

    CurrencyItem(
        "VND",
        "Vietnamese Dong",
        "₫"
    ),

    CurrencyItem(
        "LAK",
        "Lao Kip",
        "₭"
    ),

    CurrencyItem(
        "CNY",
        "Chinese Yuan",
        "¥"
    ),

    CurrencyItem(
        "JPY",
        "Japanese Yen",
        "¥"
    ),

    CurrencyItem(
        "KRW",
        "South Korean Won",
        "₩"
    ),

    CurrencyItem(
        "MYR",
        "Malaysian Ringgit",
        "RM"
    ),

    CurrencyItem(
        "SGD",
        "Singapore Dollar",
        "$"
    ),

    CurrencyItem(
        "IDR",
        "Indonesian Rupiah",
        "Rp"
    ),

    CurrencyItem(
        "PHP",
        "Philippine Peso",
        "₱"
    ),

    CurrencyItem(
        "INR",
        "Indian Rupee",
        "₹"
    )

)