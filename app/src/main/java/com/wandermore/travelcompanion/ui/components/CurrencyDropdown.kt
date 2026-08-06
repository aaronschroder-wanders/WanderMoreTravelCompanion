package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.util.supportedCurrencies
import androidx.compose.material3.ExposedDropdownMenuAnchorType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    label: String = "Currency"
) {


    var expanded by remember {
        mutableStateOf(false)
    }


    val selectedItem =
        supportedCurrencies.find {
            it.code == selectedCurrency
        }


    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }

    ) {


        OutlinedTextField(

            value = selectedItem?.let {
                "${it.code} - ${it.name}"
            } ?: selectedCurrency,

            onValueChange = {},

            readOnly = true,

            label = {
                Text(label)
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )

        )


        ExposedDropdownMenu(

            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }

        ) {


            supportedCurrencies.forEach { currency ->


                DropdownMenuItem(

                    text = {
                        Text(
                            "${currency.code} - ${currency.name}"
                        )
                    },

                    onClick = {

                        onCurrencySelected(
                            currency.code
                        )

                        expanded = false

                    }

                )

            }

        }

    }

}