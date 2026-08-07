package com.wandermore.travelcompanion.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel

@Composable
fun ExchangeRateSettingsScreen(

    exchangeRateViewModel: ExchangeRateViewModel,

    onBack: () -> Unit

) {

    val rates by exchangeRateViewModel
        .rates
        .collectAsState()


    var editingCurrency by remember {

        mutableStateOf<String?>(null)

    }


    var editedValue by remember {

        mutableStateOf("")

    }


    var showUpdatedMessage by remember {

        mutableStateOf(false)

    }



    fun saveCurrentEdit() {

        val currency = editingCurrency

        val inverseRate =
            editedValue.toDoubleOrNull()


        if (
            currency != null &&
            inverseRate != null &&
            inverseRate > 0
        ) {

            exchangeRateViewModel.updateRate(
                currency,
                inverseRate
            )

            showUpdatedMessage = true

        }


        editingCurrency = null

    }



    fun exitScreen() {

        if (editingCurrency != null) {

            saveCurrentEdit()

        }

        onBack()

    }



    BackHandler {

        exitScreen()

    }



    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()

    ) {


        Text(

            text = "Currency Exchange Rates",

            style = MaterialTheme.typography.titleLarge,

            modifier = Modifier.padding(
                bottom = 16.dp
            )

        )



        if (showUpdatedMessage) {


            Text(

                text = "✓ Rate updated",

                style = MaterialTheme.typography.bodyMedium,

                modifier = Modifier.padding(
                    bottom = 8.dp
                )

            )

        }



        LazyColumn(

            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding(),

            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {


            items(rates) { rate ->


                val currency =
                    rate.currencyCode


                val rateToNZD =
                    rate.rateToNZD



                val currentInverse =

                    if (rateToNZD > 0) {

                        1 / rateToNZD

                    } else {

                        0.0

                    }



                Card(

                    modifier = Modifier
                        .fillMaxWidth()

                ) {


                    Column(

                        modifier = Modifier
                            .padding(16.dp)

                    ) {


                        Text(

                            text = currency,

                            style =
                                MaterialTheme.typography.titleMedium

                        )


                        Text(

                            text =
                                "1 $currency = NZ$ %.4f"
                                    .format(rateToNZD)

                        )


                        Text(

                            text =
                                "1 NZD = %.4f $currency"
                                    .format(currentInverse)

                        )


                        Text(

                            text =
                                "Last updated: ${rate.lastUpdated}",

                            style =
                                MaterialTheme.typography.bodySmall

                        )



                        if (
                            editingCurrency == currency
                        ) {


                            OutlinedTextField(

                                value = editedValue,

                                onValueChange = {

                                    editedValue = it

                                },

                                label = {

                                    Text(
                                        "1 NZD = $currency"
                                    )

                                },

                                modifier = Modifier
                                    .fillMaxWidth()

                            )



                            Row(

                                modifier = Modifier
                                    .padding(
                                        top = 8.dp
                                    )

                            ) {


                                Button(

                                    onClick = {

                                        saveCurrentEdit()

                                    }

                                ) {


                                    Text(
                                        "Save"
                                    )

                                }



                                Button(

                                    onClick = {

                                        editingCurrency = null

                                    },

                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp
                                        )

                                ) {


                                    Text(
                                        "Cancel"
                                    )

                                }


                            }


                        } else {


                            Button(

                                onClick = {

                                    showUpdatedMessage = false

                                    editedValue =
                                        "%.4f".format(
                                            currentInverse
                                        )

                                    editingCurrency = currency

                                }

                            ) {


                                Text(
                                    "Edit"
                                )


                            }


                        }


                    }


                }


            }


        }



        Button(

            onClick = {

                exitScreen()

            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                )

        ) {


            Text(
                "Back"
            )


        }


    }

}