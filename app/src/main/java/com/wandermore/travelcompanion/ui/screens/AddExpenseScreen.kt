package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.ui.components.CategoryDropdown
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.ui.components.DatePickerButton
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.util.ExpenseCategories
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import java.time.LocalDate


@Composable
fun AddExpenseScreen(
    trip: Trip,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onExpenseAdded: () -> Unit
) {

    val homeCurrency by exchangeRateViewModel
        .homeCurrency
        .collectAsState()


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


    var currency by remember(homeCurrency) {
        mutableStateOf(homeCurrency)
    }


    var expenseDate by remember {
        mutableStateOf(
            LocalDate.now()
        )
    }


    val scrollState = rememberScrollState()


    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top

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


        CurrencyDropdown(

            selectedCurrency = currency,

            onCurrencySelected = {
                currency = it
            },

            label = "Expense Currency"

        )


        val previewAmount =
            amount.toDoubleOrNull()


        if (previewAmount != null) {

            // Rate used only for displaying the expense
            // in the user's current Home Currency.
            val previewRate =
                exchangeRateViewModel.getRate(currency)


            val previewHomeCurrency =
                previewAmount * previewRate


            Text(
                text = "≈ $homeCurrency %.2f"
                    .format(previewHomeCurrency)
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


        Button(

            onClick = {

                val enteredAmount =
                    amount.toDoubleOrNull()
                        ?: 0.0


                // Store the underlying reference rate:
                //
                // 1 unit of expense currency = X NZD
                //
                // This is deliberately NOT the current
                // Home Currency conversion rate.
                val exchangeRateToNZD =
                    exchangeRateViewModel
                        .getRateToNZD(currency)


                // Calculate the converted value using
                // the user's current Home Currency.
                val currentHomeRate =
                    exchangeRateViewModel
                        .getRate(currency)


                val convertedAmount =
                    enteredAmount * currentHomeRate


                val expense =
                    ExpenseEntity(

                        tripId = trip.id,

                        description = description,

                        amount = enteredAmount,

                        currency = currency,

                        category = category,

                        date = expenseDate,

                        // Store the stable NZD reference rate.
                        exchangeRate =
                            exchangeRateToNZD,

                        // Store the value displayed at the
                        // time the expense was entered.
                        convertedAmount =
                            convertedAmount,

                        numberOfNights =
                            if (
                                category == "Accommodation"
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