package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.util.ExpenseCategoryIcons
import com.wandermore.travelcompanion.util.formatMoney
import java.time.format.DateTimeFormatter


@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    onClick: () -> Unit
) {


    val dateFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy")



    val nightlyRate =
        if (
            expense.category == "Accommodation" &&
            expense.numberOfNights != null &&
            expense.numberOfNights > 0
        ) {
            expense.convertedAmount / expense.numberOfNights
        } else {
            null
        }



    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable {
                onClick()
            }

    ) {


        Column(

            modifier = Modifier.padding(10.dp)

        ) {


            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {


                Text(

                    text = ExpenseCategoryIcons.getIcon(
                        expense.category
                    ),

                    style = MaterialTheme.typography.titleMedium

                )


                Text(

                    text = expense.description,

                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(start = 6.dp)

                )


            }



            Text(

                text = expense.category,

                style = MaterialTheme.typography.bodySmall

            )



            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),

                verticalAlignment = Alignment.CenterVertically

            ) {


                Text(

                    text = formatMoney(
                        expense.amount,
                        expense.currency
                    ),

                    style = MaterialTheme.typography.bodyLarge,

                    modifier = Modifier.weight(1f)

                )


                Text(

                    text = "≈ NZ$ %.2f".format(
                        expense.convertedAmount
                    ),

                    style = MaterialTheme.typography.bodyLarge

                )

            }



            if (nightlyRate != null) {


                Text(

                    text = "${expense.numberOfNights} nights • NZ$ %.2f/night".format(
                        nightlyRate
                    ),

                    style = MaterialTheme.typography.bodySmall,

                    modifier = Modifier.padding(top = 4.dp)

                )

            }



            Text(

                text = expense.date.format(dateFormatter),

                style = MaterialTheme.typography.bodySmall,

                modifier = Modifier.padding(top = 4.dp)

            )


        }


    }


}