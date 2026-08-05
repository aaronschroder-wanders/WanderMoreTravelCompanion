package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.util.ExpenseCategories


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {


    var expanded by remember {
        mutableStateOf(false)
    }


    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {

            expanded = !expanded

        }

    ) {


        OutlinedTextField(

            value = selectedCategory,

            onValueChange = {},

            readOnly = true,

            label = {

                Text("Category")

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


            ExpenseCategories.categories.forEach { category ->


                DropdownMenuItem(

                    text = {

                        Text(category)

                    },


                    onClick = {

                        onCategorySelected(category)

                        expanded = false

                    }

                )


            }


        }


    }


}