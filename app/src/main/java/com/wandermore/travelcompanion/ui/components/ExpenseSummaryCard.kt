package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ExpenseEntity


@Composable
fun ExpenseSummaryCard(
    expenses: List<ExpenseEntity>,
    currency: String
) {


    val total =
        expenses.sumOf {
            it.convertedAmount
        }


    val expenseCount =
        expenses.size



    val accommodationExpenses =
        expenses.filter {
            it.category == "Accommodation"
        }



    val accommodationTotal =
        accommodationExpenses.sumOf {
            it.convertedAmount
        }



    val accommodationNights =
        accommodationExpenses.sumOf {

            it.numberOfNights ?: 0

        }



    val averageAccommodationNightlyRate =
        if (accommodationNights > 0) {

            accommodationTotal / accommodationNights

        } else {
            null
        }



    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {


        Column(

            modifier = Modifier
                .padding(16.dp)

        ) {


            Text(

                text = "Total Spent",

                style = MaterialTheme.typography.titleMedium

            )


            Text(

                text = "NZ$ %.2f".format(total),

                style = MaterialTheme.typography.headlineSmall

            )


            Text(

                text = "$expenseCount expenses",

                style = MaterialTheme.typography.bodyMedium

            )



            if (accommodationNights > 0) {


                Text(

                    text = "Accommodation",

                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(top = 12.dp)

                )


                Text(

                    text = "$accommodationNights nights",

                    style = MaterialTheme.typography.bodyMedium

                )


                Text(

                    text = "Average: NZ$ %.2f/night".format(
                        averageAccommodationNightlyRate
                    ),

                    style = MaterialTheme.typography.bodyMedium

                )

            }


        }

    }

}