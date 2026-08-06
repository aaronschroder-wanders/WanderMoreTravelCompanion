package com.wandermore.travelcompanion.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wandermore.travelcompanion.util.formatDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {

    var showPicker by remember {
        mutableStateOf(false)
    }


    Button(
        onClick = {
            showPicker = true
        }
    ) {

        Text(
            text = formatDate(selectedDate)
        )

    }



    if (showPicker) {


        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    selectedDate
                        .atStartOfDay(
                            ZoneId.systemDefault()
                        )
                        .toInstant()
                        .toEpochMilli()
            )


        DatePickerDialog(

            onDismissRequest = {
                showPicker = false
            },


            confirmButton = {

                Button(

                    onClick = {


                        datePickerState.selectedDateMillis?.let {

                            val newDate =
                                Instant.ofEpochMilli(it)
                                    .atZone(
                                        ZoneId.systemDefault()
                                    )
                                    .toLocalDate()


                            onDateSelected(newDate)

                        }


                        showPicker = false

                    }

                ) {

                    Text("OK")

                }

            }

        ) {


            DatePicker(
                state = datePickerState
            )

        }

    }

}