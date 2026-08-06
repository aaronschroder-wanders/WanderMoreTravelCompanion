package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.util.formatMoney


@Composable
fun TripSummaryCard(

    plannedDays: Long,

    expenseCount: Int,

    totalSpent: Double,

    currency: String,

    onClick: () -> Unit

) {


    val averagePerDay =
        if (plannedDays > 0) {

            totalSpent / plannedDays

        } else {

            0.0

        }



    Card(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 8.dp)

            .clickable {

                onClick()

            }

    ) {


        Column(

            modifier = Modifier.padding(16.dp)

        ) {


            Text(

                text = "🌏 Trip Summary",

                style = MaterialTheme.typography.titleMedium

            )



            Text(

                text = "📅 Planned duration: $plannedDays days",

                style = MaterialTheme.typography.bodyMedium

            )



            Text(

                text = "💳 Expenses: $expenseCount",

                style = MaterialTheme.typography.bodyMedium

            )



            Text(

                text = "💰 Total spent: ${
                    formatMoney(
                        totalSpent,
                        currency
                    )
                }",

                style = MaterialTheme.typography.bodyMedium

            )



            Text(

                text = "📊 Average per day: ${
                    formatMoney(
                        averagePerDay,
                        currency
                    )
                }",

                style = MaterialTheme.typography.bodyMedium

            )


            Text(

                text = "Tap for spending breakdown",

                style = MaterialTheme.typography.bodySmall

            )


        }

    }

}