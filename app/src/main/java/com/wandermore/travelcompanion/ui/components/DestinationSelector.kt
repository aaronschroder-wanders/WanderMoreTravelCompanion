package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.DestinationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationSelector(
    destinations: List<DestinationEntity>,
    selectedDestinationIds: Set<Long>,
    onSelectionChanged: (Set<Long>) -> Unit,
    onAddDestination: (String, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var newDestinationName by remember {
        mutableStateOf("")
    }

    // =========================================================
    // SELECTED DESTINATIONS
    // =========================================================

    val selectedDestinations =
        destinations.filter { destination ->
            destination.id in selectedDestinationIds
        }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        // =====================================================
        // SELECTOR
        // =====================================================

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            OutlinedTextField(
                value =
                    if (selectedDestinations.isEmpty()) {
                        ""
                    } else {
                        selectedDestinations.joinToString(", ") {
                            it.name
                        }
                    },
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Destination")
                },
                placeholder = {
                    Text("Select destination(s)")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults
                        .TrailingIcon(
                            expanded = expanded
                        )
                },
                modifier =
                    Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                minLines = 1,
                maxLines = 3
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                // -------------------------------------------------
                // EXISTING DESTINATIONS
                // -------------------------------------------------

                if (destinations.isEmpty()) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                "No destinations yet"
                            )
                        },
                        onClick = {}
                    )

                } else {

                    destinations.forEach { destination ->

                        val selected =
                            destination.id in
                                    selectedDestinationIds

                        DropdownMenuItem(

                            text = {

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Checkbox(
                                        checked = selected,
                                        onCheckedChange = null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text(
                                        destination.name
                                    )
                                }
                            },

                            onClick = {

                                val updatedSelection =
                                    if (selected) {

                                        selectedDestinationIds -
                                                destination.id

                                    } else {

                                        selectedDestinationIds +
                                                destination.id
                                    }

                                onSelectionChanged(
                                    updatedSelection
                                )
                            }
                        )
                    }
                }

                // -------------------------------------------------
                // ADD NEW DESTINATION
                // -------------------------------------------------

                DropdownMenuItem(

                    text = {
                        Text(
                            "+ Add Destination",
                            style =
                                MaterialTheme.typography
                                    .labelLarge
                        )
                    },

                    onClick = {

                        expanded = false
                        newDestinationName = ""
                        showAddDialog = true
                    }
                )
            }
        }

        // =====================================================
        // SELECTED DESTINATION SUMMARY
        // =====================================================

        if (selectedDestinations.isNotEmpty()) {

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = "Selected:",
                style =
                    MaterialTheme.typography
                        .labelMedium
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            selectedDestinations.forEach { destination ->

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 2.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "• ${destination.name}",
                        modifier =
                            Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = {

                            onSelectionChanged(
                                selectedDestinationIds -
                                        destination.id
                            )
                        }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }

    // =========================================================
    // ADD DESTINATION DIALOG
    // =========================================================

    if (showAddDialog) {

        AlertDialog(

            onDismissRequest = {
                showAddDialog = false
            },

            title = {
                Text("Add Destination")
            },

            text = {

                OutlinedTextField(
                    value = newDestinationName,
                    onValueChange = {
                        newDestinationName = it
                    },
                    label = {
                        Text("Destination")
                    },
                    singleLine = true,
                    modifier =
                        Modifier.fillMaxWidth()
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        val trimmedName =
                            newDestinationName.trim()

                        if (trimmedName.isNotBlank()) {

                            onAddDestination(
                                trimmedName
                            ) { success ->

                                if (success) {
                                    showAddDialog = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Add")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showAddDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}