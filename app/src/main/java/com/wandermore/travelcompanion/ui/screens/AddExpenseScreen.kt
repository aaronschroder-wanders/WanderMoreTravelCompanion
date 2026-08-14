package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.ui.components.CategoryDropdown
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.ui.components.DatePickerButton
import com.wandermore.travelcompanion.util.ExpenseCategories
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate

@Composable
fun AddExpenseScreen(
    trip: Trip,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onExpenseAdded: () -> Unit
) {

    // =========================================================
    // TRIP HOME CURRENCY
    //
    // This is the currency that belongs to THIS trip.
    // It must not change if the user's global Home Currency
    // setting is changed later.
    // =========================================================

    val tripHomeCurrency =
        trip.homeCurrency

    var description by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf(
            ExpenseCategories.categories.first()
        )
    }

    var numberOfNights by remember {
        mutableStateOf("")
    }

    var currency by remember(tripHomeCurrency) {
        mutableStateOf(tripHomeCurrency)
    }

    var expenseDate by remember {
        mutableStateOf(
            LocalDate.now()
        )
    }

    val scrollState =
        rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Top
    ) {

        Text(
            text = "Add Expense"
        )

        Text(
            text = "Trip: ${trip.name}"
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("Description")
            }
        )

        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
            },
            label = {
                Text("Amount")
            }
        )

        // =====================================================
        // CURRENCY
        // =====================================================

        CurrencyDropdown(
            selectedCurrency = currency,

            onCurrencySelected = {
                currency = it
            },

            label = "Expense Currency"
        )

        // =====================================================
        // CONVERSION PREVIEW
        //
        // Always convert into THIS TRIP'S home currency.
        // =====================================================

        val previewAmount =
            amount.toDoubleOrNull()

        if (previewAmount != null) {

            val expenseRateToNZD =
                exchangeRateViewModel.getRateToNZD(
                    currency
                )

            val tripHomeRateToNZD =
                exchangeRateViewModel.getRateToNZD(
                    tripHomeCurrency
                )

            val previewRate =
                if (currency == tripHomeCurrency) {
                    1.0
                } else if (
                    expenseRateToNZD > 0.0 &&
                    tripHomeRateToNZD > 0.0
                ) {
                    expenseRateToNZD /
                            tripHomeRateToNZD
                } else {
                    0.0
                }

            val previewHomeCurrency =
                previewAmount * previewRate

            Text(
                text =
                    "≈ $tripHomeCurrency %.2f"
                        .format(
                            previewHomeCurrency
                        )
            )
        }

        CategoryDropdown(
            selectedCategory = category,

            onCategorySelected = {
                category = it
            }
        )

        if (category == "Accommodation") {

            OutlinedTextField(
                value = numberOfNights,

                onValueChange = {
                    numberOfNights = it
                },

                label = {
                    Text("Number of nights")
                }
            )
        }

        DatePickerButton(
            selectedDate = expenseDate,

            onDateSelected = {
                expenseDate = it
            }
        )

        // =====================================================
        // SAVE
        // =====================================================

        Button(
            onClick = {

                val enteredAmount =
                    amount.toDoubleOrNull()
                        ?: 0.0

                // -------------------------------------------------
                // Store the stable underlying reference rate:
                //
                // 1 unit of expense currency = X NZD
                // -------------------------------------------------

                val exchangeRateToNZD =
                    exchangeRateViewModel
                        .getRateToNZD(currency)

                // -------------------------------------------------
                // Convert into THIS TRIP'S home currency.
                //
                // getRate() normally uses the current global
                // Home Currency, so we cannot use it here when
                // the global setting may differ from the trip.
                //
                // Calculate the trip-home rate directly from the
                // underlying NZD reference rates.
                // -------------------------------------------------

                val tripHomeRateToNZD =
                    exchangeRateViewModel
                        .getRateToNZD(
                            tripHomeCurrency
                        )

                val expenseRateToTripHome =
                    if (
                        currency ==
                        tripHomeCurrency
                    ) {
                        1.0
                    } else if (
                        tripHomeRateToNZD > 0.0 &&
                        exchangeRateToNZD > 0.0
                    ) {
                        exchangeRateToNZD /
                                tripHomeRateToNZD
                    } else {
                        0.0
                    }

                val convertedAmount =
                    enteredAmount *
                            expenseRateToTripHome

                val expense =
                    ExpenseEntity(

                        tripId =
                            trip.id,

                        description =
                            description,

                        amount =
                            enteredAmount,

                        currency =
                            currency,

                        category =
                            category,

                        date =
                            expenseDate,

                        // Stable currency → NZD reference rate.
                        exchangeRate =
                            exchangeRateToNZD,

                        // Amount converted into this trip's
                        // permanent home currency.
                        convertedAmount =
                            convertedAmount,

                        numberOfNights =
                            if (
                                category ==
                                "Accommodation"
                            ) {

                                numberOfNights
                                    .toIntOrNull()

                            } else {

                                null

                            }
                    )

                tripViewModel.addExpense(
                    expense
                )

                onExpenseAdded()
            }
        ) {

            Text(
                text = "Save Expense"
            )
        }
    }
}