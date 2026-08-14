package com.wandermore.travelcompanion.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


fun formatMoney(
    amount: Double,
    currencyCode: String
): String {

    // ---------------------------------------------------------
    // NZD needs an explicit symbol because the default locale
    // formats it simply as "$".
    // ---------------------------------------------------------

    if (currencyCode == "NZD") {

        val decimals =
            currencyDecimalPlaces(
                currencyCode
            )

        return "NZ$ " + String.format(
            Locale.US,
            "%,.${decimals}f",
            amount
        )
    }

    // ---------------------------------------------------------
    // All other currencies use the standard currency formatter.
    // ---------------------------------------------------------

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