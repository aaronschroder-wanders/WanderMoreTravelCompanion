package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel

@Composable
fun InitialExchangeRateSetupScreen(

    exchangeRateViewModel: ExchangeRateViewModel,

    onComplete: () -> Unit

) {

    val homeCurrency by exchangeRateViewModel
        .homeCurrency
        .collectAsState()


    var refreshMessage by remember {

        mutableStateOf<String?>(null)

    }


    var refreshing by remember {

        mutableStateOf(false)

    }


    // =========================================================
    // AUTOMATIC INITIAL RATE DOWNLOAD
    // =========================================================
    //
    // This screen is only reached after the user has selected
    // their Home Currency.
    //
    // Automatically download the exchange rates when the
    // screen is first displayed.
    //
    // If the download fails, the user remains on this screen
    // and can retry manually.
    // =========================================================

    LaunchedEffect(Unit) {

        refreshing = true

        refreshMessage = null

        exchangeRateViewModel.refreshRates { success ->

            refreshing = false

            if (success) {

                refreshMessage =
                    "✓ Exchange rates downloaded successfully."

                onComplete()

            } else {

                refreshMessage =
                    "⚠ Unable to download exchange rates. " +
                            "Please check your internet connection " +
                            "and try again."

            }

        }

    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement =
            Arrangement.spacedBy(20.dp)

    ) {

        // =========================================================
        // TITLE
        // =========================================================

        Text(

            text = "Currency Rates",

            style =
                MaterialTheme.typography.headlineMedium

        )


        // =========================================================
        // INTRODUCTION
        // =========================================================

        Text(

            text =
                "Your Home Currency is $homeCurrency.",

            style =
                MaterialTheme.typography.titleMedium

        )


        Text(

            text =
                "Wander More needs the latest exchange rates " +
                        "to calculate expenses and currency conversions.",

            style =
                MaterialTheme.typography.bodyLarge

        )


        Text(

            text =
                "An internet connection is required for this " +
                        "initial download.",

            style =
                MaterialTheme.typography.bodyMedium

        )


        // =========================================================
        // MESSAGE
        // =========================================================

        refreshMessage?.let { message ->

            Text(

                text = message,

                style =
                    MaterialTheme.typography.bodyMedium

            )

        }


        // =========================================================
        // REFRESH / RETRY
        // =========================================================

        Button(

            onClick = {

                refreshing = true

                refreshMessage = null

                exchangeRateViewModel.refreshRates { success ->

                    refreshing = false

                    if (success) {

                        refreshMessage =
                            "✓ Exchange rates downloaded successfully."

                        onComplete()

                    } else {

                        refreshMessage =
                            "⚠ Unable to download exchange rates. " +
                                    "Please check your internet connection " +
                                    "and try again."

                    }

                }

            },

            enabled = !refreshing,

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(

                text =
                    if (refreshing) {

                        "Refreshing..."

                    } else {

                        "Download Exchange Rates"

                    }

            )

        }

    }

}