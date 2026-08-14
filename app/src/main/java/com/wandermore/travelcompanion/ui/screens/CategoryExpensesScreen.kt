package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.ExpenseCard
import com.wandermore.travelcompanion.util.formatMoney
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun CategoryExpensesScreen(

    tripId: Long,

    category: String,

    currency: String,

    tripViewModel: TripViewModel,

    onEditExpense: (Long) -> Unit,

    onBack: () -> Unit

) {

    val expenses by tripViewModel
        .getExpensesForTrip(tripId)
        .collectAsState(
            initial = emptyList()
        )


    val categoryExpenses =
        expenses.filter {

            it.category == category

        }


    val total =
        categoryExpenses.sumOf {

            it.convertedAmount

        }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Top

    ) {

        Text(

            text = category,

            style =
                MaterialTheme.typography.titleLarge

        )


        Text(

            text = formatMoney(
                total,
                currency
            ),

            style =
                MaterialTheme.typography.titleMedium,

            modifier =
                Modifier.padding(
                    vertical = 12.dp
                )

        )


        LazyColumn(

            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()

        ) {

            items(categoryExpenses) { expense ->

                ExpenseCard(

                    expense = expense,

                    // The currency parameter is the
                    // home currency belonging to this trip.
                    homeCurrency = currency,

                    onClick = {

                        onEditExpense(
                            expense.id
                        )

                    }

                )

            }

        }


        Button(

            onClick = {

                onBack()

            }

        ) {

            Text(
                "Back to Breakdown"
            )

        }

    }

}