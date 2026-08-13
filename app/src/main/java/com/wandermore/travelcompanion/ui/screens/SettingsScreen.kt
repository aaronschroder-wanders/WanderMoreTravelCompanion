package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel

@Composable
fun SettingsScreen(

    userSettingsViewModel: UserSettingsViewModel,

    onExchangeRates: () -> Unit,

    onBackup: () -> Unit,

    onRestore: () -> Unit,

    onBack: () -> Unit

) {

    val homeCurrency by userSettingsViewModel
        .homeCurrency
        .collectAsState()


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        // =========================================================
        // TITLE
        // =========================================================

        Text(

            text = "Settings",

            style =
                MaterialTheme.typography.headlineMedium

        )


        // =========================================================
        // CURRENCY
        // =========================================================

        Text(

            text = "CURRENCY",

            style =
                MaterialTheme.typography.labelLarge,

            modifier = Modifier.padding(
                top = 8.dp
            )

        )


        // =========================================================
        // HOME CURRENCY
        // =========================================================

        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Column(

                modifier =
                    Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)

            ) {

                Text(

                    text = "Home Currency",

                    style =
                        MaterialTheme.typography.titleMedium

                )


                Text(

                    text =
                        "Used as the currency for new trips and calculations."

                )


                CurrencyDropdown(

                    selectedCurrency =
                        homeCurrency,

                    onCurrencySelected = { currency ->

                        userSettingsViewModel
                            .setHomeCurrency(currency)

                    },

                    label = "Home Currency"

                )

            }

        }


        // =========================================================
        // EXCHANGE RATES
        // =========================================================

        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Button(

                onClick =
                    onExchangeRates,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        "Currency Exchange Rates"

                )

            }

        }


        // =========================================================
        // DATA & BACKUP
        // =========================================================

        Text(

            text = "DATA & BACKUP",

            style =
                MaterialTheme.typography.labelLarge,

            modifier = Modifier.padding(
                top = 8.dp
            )

        )


        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Button(

                onClick =
                    onBackup,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        "Backup to Google Drive"

                )

            }

        }


        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Button(

                onClick =
                    onRestore,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        "Restore from Google Drive"

                )

            }

        }


        // =========================================================
        // SPACER
        // Pushes Back button to bottom
        // =========================================================

        Column(

            modifier =
                Modifier.weight(1f)

        ) {
        }


        // =========================================================
        // BACK
        // =========================================================

        Button(

            onClick =
                onBack

        ) {

            Text(

                text =
                    "Back"

            )

        }

    }

}