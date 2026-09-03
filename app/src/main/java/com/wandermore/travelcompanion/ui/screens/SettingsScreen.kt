package com.wandermore.travelcompanion.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel

@Composable
fun SettingsScreen(

    userSettingsViewModel: UserSettingsViewModel,

    onExchangeRates: () -> Unit,

    onDestinations: () -> Unit,

    onBackup: () -> Unit,

    onRestore: () -> Unit,

    onBack: () -> Unit

) {

    val homeCurrency by userSettingsViewModel
        .homeCurrency
        .collectAsState()

    val context = LocalContext.current

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)

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
                top = 4.dp
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
                    Modifier.padding(12.dp),

                verticalArrangement =
                    Arrangement.spacedBy(4.dp)

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
        // DESTINATIONS
        // =========================================================

        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Button(

                onClick =
                    onDestinations,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        "Destinations"

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
                top = 4.dp
            )

        )


        // =========================================================
        // BACKUP
        // =========================================================

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


        // =========================================================
        // RESTORE
        // =========================================================

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
        // WANDER MORE WORK LESS
        // =========================================================

        Card(

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Column(

                modifier =
                    Modifier.padding(12.dp),

                verticalArrangement =
                    Arrangement.spacedBy(6.dp)

            ) {

                Text(

                    text =
                        "Wander More Work Less",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF00A6A6)

                )


                Text(

                    text =
                        "Travel videos, tips and adventures.",

                    style =
                        MaterialTheme.typography.bodyLarge

                )


                Button(

                    onClick = {

                        val intent = Intent(

                            Intent.ACTION_VIEW,

                            Uri.parse(
                                "https://www.youtube.com/@WanderMoreWorkLess"
                            )

                        )

                        context.startActivity(intent)

                    },

                    modifier =
                        Modifier.fillMaxWidth()

                ) {

                    Text(

                        text =
                            "Visit our YouTube Channel"

                    )

                }

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