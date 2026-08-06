package com.wandermore.travelcompanion.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


fun formatMoney(
    amount: Double,
    currencyCode: String
): String {


    val formatter =
        NumberFormat.getCurrencyInstance(
            Locale.getDefault()
        )


    formatter.currency =
        Currency.getInstance(currencyCode)


    val decimals =
        currencyDecimalPlaces(
            currencyCode
        )


    formatter.maximumFractionDigits = decimals

    formatter.minimumFractionDigits = decimals


    return formatter.format(amount)

}