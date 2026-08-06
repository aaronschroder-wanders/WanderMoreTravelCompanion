package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.util.ExpenseCategoryIcons
import com.wandermore.travelcompanion.util.formatMoney
import com.wandermore.travelcompanion.viewmodel.TripViewModel


@Composable
fun CategoryBreakdownScreen(

    tripId: Long,

    currency: String,

    tripViewModel: TripViewModel,

    onBack: () -> Unit

) {


    val expenses by tripViewModel
        .getExpensesForTrip(tripId)
        .collectAsState(
            initial = emptyList()
        )



    val totalSpent =
        expenses.sumOf {

            it.convertedAmount

        }



    val categoryTotals =
        expenses
            .groupBy {

                it.category

            }
            .mapValues { entry ->

                entry.value.sumOf {

                    it.convertedAmount

                }

            }
            .toList()
            .sortedByDescending {

                it.second

            }



    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top

    ) {



        Text(

            text = "📊 Spending Breakdown",

            style = MaterialTheme.typography.titleLarge

        )



        Text(

            text = "Total spent: ${
                formatMoney(
                    totalSpent,
                    currency
                )
            }",

            style = MaterialTheme.typography.titleMedium,

            modifier = Modifier.padding(
                vertical = 16.dp
            )

        )



        LazyColumn(

            modifier = Modifier.weight(1f)

        ) {


            items(categoryTotals) { categoryTotal ->



                val percentage =

                    if (totalSpent > 0) {

                        (categoryTotal.second / totalSpent * 100)

                    } else {

                        0.0

                    }



                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 6.dp
                        )

                ) {


                    Column(

                        modifier = Modifier.padding(16.dp)

                    ) {



                        Text(

                            text =
                                "${ExpenseCategoryIcons.getIcon(
                                    categoryTotal.first
                                )} ${categoryTotal.first}",

                            style = MaterialTheme.typography.titleMedium

                        )



                        Text(

                            text =
                                formatMoney(
                                    categoryTotal.second,
                                    currency
                                ),

                            style = MaterialTheme.typography.bodyLarge

                        )



                        Text(

                            text =
                                "%.1f%% of trip spending"
                                    .format(
                                        percentage
                                    ),

                            style = MaterialTheme.typography.bodySmall

                        )


                    }


                }


            }


        }



        Button(

            onClick = {

                onBack()

            }

        ) {

            Text(
                text = "Back to Trip"
            )

        }


    }


}