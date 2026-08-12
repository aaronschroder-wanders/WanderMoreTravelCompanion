package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.TripEstimateEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.ui.components.CategoryDropdown
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun AddTripEstimateScreen(
    trip: Trip,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onEstimateAdded: () -> Unit,
    onBack: () -> Unit
) {

    var category by remember {
        mutableStateOf("")
    }

    var estimateType by remember {
        mutableStateOf("ONE_OFF")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var currency by remember {
        mutableStateOf(trip.homeCurrency)
    }

    var notes by remember {
        mutableStateOf("")
    }

    var showDuplicateDialog by remember {
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()

    // =========================================================
    // DUPLICATE ESTIMATE DIALOG
    // =========================================================

    if (showDuplicateDialog) {

        AlertDialog(
            onDismissRequest = {
                showDuplicateDialog = false
            },

            title = {
                Text(
                    text = "Estimate already exists"
                )
            },

            text = {
                Text(
                    text =
                        "You already have an estimate for " +
                                "\"$category\" on this trip. " +
                                "Please edit the existing estimate " +
                                "instead."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDuplicateDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Add Trip Estimate",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = trip.name,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =====================================================
        // CATEGORY
        // =====================================================

        CategoryDropdown(
            selectedCategory = category,
            onCategorySelected = {
                category = it
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // ESTIMATE BASIS
        // =====================================================

        Text(
            text = "Estimate basis",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Button(
            onClick = {
                estimateType = "ONE_OFF"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (estimateType == "ONE_OFF")
                    "✓ One-off"
                else
                    "One-off"
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Button(
            onClick = {
                estimateType = "PER_DAY"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (estimateType == "PER_DAY")
                    "✓ Per day"
                else
                    "Per day"
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Button(
            onClick = {
                estimateType = "PER_NIGHT"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (estimateType == "PER_NIGHT")
                    "✓ Per night"
                else
                    "Per night"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // AMOUNT
        // =====================================================

        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->

                    if (!focusState.isFocused) {

                        val value =
                            amount.toDoubleOrNull()

                        if (value != null) {
                            amount =
                                "%.2f".format(value)
                        }
                    }
                },
            label = {
                Text("Amount")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // CURRENCY
        // =====================================================

        CurrencyDropdown(
            selectedCurrency = currency,
            onCurrencySelected = {
                currency = it
            },
            label = "Estimate Currency"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // CONVERTED AMOUNT PREVIEW
        // =====================================================

        val enteredAmount =
            amount.toDoubleOrNull()

        if (enteredAmount != null) {

            val exchangeRate =
                exchangeRateViewModel.getRate(currency)

            val convertedAmount =
                enteredAmount * exchangeRate

            Text(
                text =
                    "≈ NZ$ %.2f".format(
                        convertedAmount
                    ),
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // NOTES
        // =====================================================

        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Notes")
            },
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            // CANCEL - LEFT
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            // SAVE - RIGHT
            Button(
                onClick = {

                    val enteredAmount =
                        amount.toDoubleOrNull()
                            ?: 0.0

                    val exchangeRate =
                        exchangeRateViewModel.getRate(
                            currency
                        )

                    val convertedAmount =
                        enteredAmount * exchangeRate

                    val estimate =
                        TripEstimateEntity(

                            tripId = trip.id,

                            category = category,

                            estimateType = estimateType,

                            amount = enteredAmount,

                            currency = currency,

                            convertedAmount = convertedAmount,

                            notes = notes.trim()
                                .ifBlank {
                                    null
                                }
                        )

                    tripViewModel.addTripEstimate(
                        estimate = estimate,
                        onAdded = onEstimateAdded,
                        onDuplicate = {
                            showDuplicateDialog = true
                        }
                    )
                },
                modifier = Modifier.weight(1f),
                enabled =
                    category.isNotBlank() &&
                            enteredAmount != null &&
                            enteredAmount > 0.0
            ) {
                Text("Save")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}