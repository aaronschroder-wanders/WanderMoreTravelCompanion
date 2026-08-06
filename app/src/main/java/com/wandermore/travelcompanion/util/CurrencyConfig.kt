package com.wandermore.travelcompanion.util


fun currencyDecimalPlaces(
    currency: String
): Int {

    return when (currency) {

        "JPY",
        "KRW",
        "VND" -> 0

        else -> 2

    }

}