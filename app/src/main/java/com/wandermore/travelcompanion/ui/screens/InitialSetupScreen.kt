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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel

@Composable
fun InitialSetupScreen(

    userSettingsViewModel: UserSettingsViewModel,

    exchangeRateViewModel: ExchangeRateViewModel,

    onComplete: () -> Unit

) {

    val homeCurrency by userSettingsViewModel
        .homeCurrency
        .collectAsState()


    var refreshing by remember {

        mutableStateOf(false)

    }


    var message by remember {

        mutableStateOf<String?>(null)

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

            text = "Welcome to Wander More",

            style =
                MaterialTheme.typography.headlineMedium

        )


        // =========================================================
        // INTRODUCTION
        // =========================================================

        Text(

            text =
                "Before you get started, please choose your Home Currency.",

            style =
                MaterialTheme.typography.bodyLarge

        )


        Text(

            text =
                "Your Home Currency is used for trip calculations, " +
                        "expenses and currency conversions.",

            style =
                MaterialTheme.typography.bodyMedium

        )


        // =========================================================
        // HOME CURRENCY
        // =========================================================

        Text(

            text = "Home Currency",

            style =
                MaterialTheme.typography.titleMedium

        )


        CurrencyDropdown(

            selectedCurrency =
                homeCurrency,

            onCurrencySelected = { currency ->

                refreshing = true

                message = null

                userSettingsViewModel.setHomeCurrency(
                    currency
                ) {

                    exchangeRateViewModel.refreshRates { success ->

                        refreshing = false

                        if (success) {

                            message =
                                "✓ Exchange rates downloaded successfully."

                        } else {

                            message =
                                "⚠ Unable to download exchange rates. " +
                                        "Please check your internet connection " +
                                        "and try again."

                        }

                    }

                }

            },

            label = "Home Currency"

        )


        // =========================================================
        // STATUS MESSAGE
        // =========================================================

        message?.let { text ->

            Text(

                text = text,

                style =
                    MaterialTheme.typography.bodyMedium

            )

        }


        // =========================================================
        // CONTINUE
        // =========================================================

        Button(

            onClick = {

                onComplete()

            },

            enabled = !refreshing,

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(

                text =
                    if (refreshing) {

                        "Downloading Exchange Rates..."

                    } else {

                        "Continue"

                    }

            )

        }

    }

}