package com.wandermore.travelcompanion.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.ItineraryLinkEntity
import com.wandermore.travelcompanion.ui.components.DestinationSelector
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.net.URI
import java.net.URISyntaxException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItineraryScreen(
    itineraryId: Long,
    tripViewModel: TripViewModel,
    onItineraryUpdated: () -> Unit,
    onDeleteItinerary: (ItineraryEntity) -> Unit,
    onBack: () -> Unit
) {

    // =========================================================
    // FORM STATE
    // =========================================================

    var existingItem by remember {
        mutableStateOf<ItineraryEntity?>(null)
    }

    var date by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var time by remember {
        mutableStateOf<LocalTime?>(null)
    }

    var title by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("")
    }

    var nightsText by remember {
        mutableStateOf("")
    }

    /*
     * LOCATION
     *
     * Retained internally for backwards compatibility and
     * existing database records, but deliberately hidden from
     * the user interface.
     */
    var location by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var booked by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // LINKS / DOCUMENTS STATE
    // =========================================================

    val links = remember {
        mutableStateListOf<ItineraryLinkDraft>()
    }

    var linkLabel by remember {
        mutableStateOf("")
    }

    var linkUrl by remember {
        mutableStateOf("")
    }

    var linkError by remember {
        mutableStateOf("")
    }

    var editingLinkId by remember {
        mutableStateOf<Long?>(null)
    }

    var showLinkEditor by remember {
        mutableStateOf(false)
    }

    var linksLoaded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()

    /*
     * One requester for the whole document editor.
     *
     * This is deliberately attached to the editor container
     * rather than only to the Update/Add button. That means
     * focusing either the label or URL asks Compose to bring
     * the entire editor into view.
     */
    val linkEditorBringIntoViewRequester =
        remember {
            BringIntoViewRequester()
        }

    // =========================================================
    // DESTINATION STATE
    // =========================================================

    var selectedDestinationIds by remember(
        itineraryId
    ) {
        mutableStateOf<Set<Long>>(emptySet())
    }

    var destinationsLoaded by remember(
        itineraryId
    ) {
        mutableStateOf(false)
    }

    // =========================================================
    // DIALOG STATE
    // =========================================================

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // ITINERARY TYPES
    // =========================================================

    val itineraryTypes = listOf(
        "Travel",
        "Accommodation",
        "Activity",
        "Attraction",
        "Arrival",
        "Departure",
        "Other"
    )

    // =========================================================
    // LOAD EXISTING ITEM
    // =========================================================

    LaunchedEffect(itineraryId) {

        val item =
            tripViewModel.getItineraryById(
                itineraryId
            )

        if (item != null) {

            existingItem = item

            date = item.date
            time = item.time
            title = item.title
            type = item.type
            nightsText = item.nights?.toString() ?: ""
            location = item.location ?: ""
            notes = item.notes ?: ""
            booked = item.booked

            selectedDestinationIds =
                tripViewModel
                    .getDestinationIdsForItinerary(
                        item.id
                    )
                    .toSet()

            destinationsLoaded = true
        }
    }

    // =========================================================
    // EXISTING LINKS / DOCUMENTS
    // =========================================================

    val savedLinks by
    tripViewModel
        .getItineraryLinks(itineraryId)
        .collectAsState(
            initial = emptyList()
        )

    LaunchedEffect(itineraryId) {

        val initialLinks =
            tripViewModel
                .getItineraryLinks(itineraryId)
                .first()

        if (initialLinks.isNotEmpty()) {

            links.clear()

            initialLinks.forEach { link ->

                links.add(
                    ItineraryLinkDraft(
                        id = link.id,
                        label = link.label,
                        url = link.url
                    )
                )
            }

        } else {

            /*
             * Backwards compatibility for an itinerary item
             * that still has the old single webLink value.
             */
            val item =
                tripViewModel.getItineraryById(
                    itineraryId
                )

            if (
                item != null &&
                !item.webLink.isNullOrBlank()
            ) {

                links.clear()

                links.add(
                    ItineraryLinkDraft(
                        id = 0,
                        label = "Web Link",
                        url = item.webLink
                    )
                )
            }
        }

        linksLoaded = true
    }

    // =========================================================
    // CURRENT ITEM
    // =========================================================

    if (existingItem == null) {
        return
    }

    val currentItem =
        existingItem!!

    // =========================================================
    // CREATED FROM ACTIVITY
    // =========================================================

    val createdFromActivity =
        currentItem.activityId != null

    // =========================================================
    // ALL ACTIVE GLOBAL DESTINATIONS
    // =========================================================

    val destinations by
    tripViewModel
        .getAllActiveDestinations()
        .collectAsState(
            initial = emptyList()
        )

    // =========================================================
    // DATE / TIME FORMATTERS
    // =========================================================

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )

    val timeFormatter =
        DateTimeFormatter.ofPattern(
            "HH:mm"
        )

    // =========================================================
    // LINK FUNCTIONS
    // =========================================================

    fun clearLinkEditor() {

        linkLabel = ""
        linkUrl = ""
        linkError = ""
        editingLinkId = null
        showLinkEditor = false
    }

    fun addOrUpdateLink() {

        val cleanedLabel =
            linkLabel.trim()

        val cleanedUrl =
            linkUrl.trim()

        if (cleanedLabel.isBlank()) {

            linkError =
                "Please enter a label."

            return
        }

        if (!isValidWebLink(cleanedUrl)) {

            linkError =
                "Please enter a valid web link starting with https://"

            return
        }

        val existingId =
            editingLinkId

        if (existingId == null) {

            links.add(
                ItineraryLinkDraft(
                    id = 0,
                    label = cleanedLabel,
                    url = cleanedUrl
                )
            )

        } else {

            val index =
                links.indexOfFirst {
                    it.id == existingId
                }

            if (index >= 0) {

                links[index] =
                    ItineraryLinkDraft(
                        id = existingId,
                        label = cleanedLabel,
                        url = cleanedUrl
                    )
            }
        }

        clearLinkEditor()
    }

    fun editLink(
        link: ItineraryLinkDraft
    ) {

        editingLinkId =
            link.id

        linkLabel =
            link.label

        linkUrl =
            link.url

        linkError =
            ""

        showLinkEditor =
            true
    }

    fun startAddingLink() {

        linkLabel = ""
        linkUrl = ""
        linkError = ""
        editingLinkId = null
        showLinkEditor = true
    }

    // =========================================================
    // BRING DOCUMENT EDITOR INTO VIEW
    // =========================================================

    LaunchedEffect(showLinkEditor) {

        if (showLinkEditor) {

            /*
             * Give Compose a moment to place the editor in
             * the layout before requesting it to scroll into
             * view.
             */
            kotlinx.coroutines.delay(100)

            linkEditorBringIntoViewRequester
                .bringIntoView()
        }
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {

        // =====================================================
        // SCROLLABLE FORM
        // =====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            // =================================================
            // HEADER
            // =================================================

            Text(
                text = "Edit Itinerary Item",
                style =
                    MaterialTheme.typography
                        .headlineMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "Update this travel plan, stay or event.",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            // =================================================
            // DATE
            // =================================================

            OutlinedButton(
                onClick = {
                    showDatePicker = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (date == null) {
                            "Select date"
                        } else {
                            date!!.format(
                                dateFormatter
                            )
                        }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // TIME
            // =================================================

            OutlinedButton(
                onClick = {
                    showTimePicker = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (time == null) {
                            "Select time — optional"
                        } else {
                            time!!.format(
                                timeFormatter
                            )
                        }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // TITLE
            // =================================================

            OutlinedTextField(
                value = title,

                onValueChange = {
                    title = it
                },

                label = {
                    Text("Title")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // TYPE
            // =================================================

            ExposedDropdownMenuBox(
                expanded =
                    typeExpanded,

                onExpandedChange = {
                    typeExpanded =
                        !typeExpanded
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = type,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Type")
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    typeExpanded
                            )
                    },

                    modifier =
                        Modifier
                            .menuAnchor()
                            .fillMaxWidth(),

                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded =
                        typeExpanded,

                    onDismissRequest = {
                        typeExpanded =
                            false
                    }
                ) {

                    itineraryTypes.forEach {
                            itineraryType ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    itineraryType
                                )
                            },

                            onClick = {

                                type =
                                    itineraryType

                                typeExpanded =
                                    false
                            }
                        )
                    }
                }
            }

            // =================================================
            // BOOKED / CREATED FROM ACTIVITY
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            if (createdFromActivity) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            "Created from Activity",

                        style =
                            MaterialTheme.typography
                                .bodyLarge,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            MaterialTheme.colorScheme
                                .primary
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "This itinerary item was created from a booked Activity or Attraction.",

                        style =
                            MaterialTheme.typography
                                .bodySmall,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

            } else {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Booked",

                            style =
                                MaterialTheme.typography
                                    .bodyLarge
                        )

                        Text(
                            text =
                                if (booked) {
                                    "This item is booked"
                                } else {
                                    "Not booked yet"
                                },

                            style =
                                MaterialTheme.typography
                                    .bodySmall,

                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = booked,

                        onCheckedChange = {
                            booked = it
                        }
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // NIGHTS
            // =================================================

            OutlinedTextField(
                value = nightsText,

                onValueChange = {

                    if (
                        it.all { character ->
                            character.isDigit()
                        }
                    ) {
                        nightsText = it
                    }
                },

                label = {
                    Text("Nights")
                },

                placeholder = {
                    Text("Optional")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // =================================================
            // DESTINATIONS
            // =================================================

            if (destinationsLoaded) {

                DestinationSelector(

                    destinations =
                        destinations,

                    selectedDestinationIds =
                        selectedDestinationIds,

                    onSelectionChanged = {
                        selectedDestinationIds =
                            it
                    },

                    onAddDestination = {
                            destinationName,
                            onResult ->

                        tripViewModel
                            .addDestinationAndReturnId(
                                destinationName
                            ) { destinationId ->

                                if (
                                    destinationId != null
                                ) {

                                    selectedDestinationIds =
                                        selectedDestinationIds +
                                                destinationId

                                    onResult(true)

                                } else {

                                    onResult(false)
                                }
                            }
                    }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // =================================================
            // LINKS / DOCUMENTS
            // =================================================

            Text(
                text = "Links / Documents",

                style =
                    MaterialTheme.typography
                        .labelLarge
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "Add tickets, confirmations, timetables or other useful documents.",

                style =
                    MaterialTheme.typography
                        .bodySmall,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // EXISTING LINKS
            // =================================================

            links.forEach { link ->

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 8.dp
                            ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = link.label,

                            style =
                                MaterialTheme.typography
                                    .bodyLarge,

                            fontWeight =
                                FontWeight.SemiBold,

                            modifier =
                                Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = {

                                openWebLink(
                                    context,
                                    link.url
                                )
                            }
                        ) {
                            Text("Open")
                        }

                        TextButton(
                            onClick = {
                                editLink(link)
                            }
                        ) {
                            Text("Edit")
                        }

                        TextButton(
                            onClick = {

                                links.remove(link)

                                if (
                                    editingLinkId ==
                                    link.id
                                ) {
                                    clearLinkEditor()
                                }
                            }
                        ) {
                            Text(
                                text = "Delete",
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .error
                            )
                        }
                    }
                }
            }

            // =================================================
            // ADD LINK BUTTON
            // =================================================

            if (!showLinkEditor) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Button(
                    onClick = {
                        startAddingLink()
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        "+ Add Link / Document"
                    )
                }
            }

            // =================================================
            // LINK EDITOR
            // =================================================

            if (showLinkEditor) {

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(
                                linkEditorBringIntoViewRequester
                            ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                    ) {

                        Text(
                            text =
                                if (
                                    editingLinkId == null
                                ) {
                                    "Add Link / Document"
                                } else {
                                    "Edit Link / Document"
                                },

                            style =
                                MaterialTheme.typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        OutlinedTextField(
                            value =
                                linkLabel,

                            onValueChange = {
                                linkLabel = it
                                linkError = ""
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .onFocusEvent {
                                            focusState ->

                                        if (
                                            focusState.isFocused
                                        ) {

                                            coroutineScope
                                                .launch {

                                                    kotlinx
                                                        .coroutines
                                                        .delay(100)

                                                    linkEditorBringIntoViewRequester
                                                        .bringIntoView()
                                                }
                                        }
                                    },

                            label = {
                                Text(
                                    "Document label"
                                )
                            },

                            placeholder = {
                                Text(
                                    "e.g. Aaron — Train ticket"
                                )
                            },

                            singleLine = true
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value =
                                linkUrl,

                            onValueChange = {
                                linkUrl = it
                                linkError = ""
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .onFocusEvent {
                                            focusState ->

                                        if (
                                            focusState.isFocused
                                        ) {

                                            coroutineScope
                                                .launch {

                                                    kotlinx
                                                        .coroutines
                                                        .delay(100)

                                                    linkEditorBringIntoViewRequester
                                                        .bringIntoView()
                                                }
                                        }
                                    },

                            label = {
                                Text("Web link")
                            },

                            placeholder = {
                                Text(
                                    "Paste web link"
                                )
                            },

                            singleLine = true,

                            isError =
                                linkError.isNotBlank()
                        )

                        if (
                            linkError.isNotBlank()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    linkError,

                                style =
                                    MaterialTheme.typography
                                        .bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .error
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                addOrUpdateLink()
                            },

                            enabled =
                                linkLabel
                                    .trim()
                                    .isNotEmpty() &&
                                        linkUrl
                                            .trim()
                                            .isNotEmpty(),

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text(
                                if (
                                    editingLinkId == null
                                ) {
                                    "+ Add Link / Document"
                                } else {
                                    "Update Link / Document"
                                }
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                clearLinkEditor()
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text("Cancel")
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // =================================================
            // NOTES
            // =================================================

            OutlinedTextField(
                value = notes,

                onValueChange = {
                    notes = it
                },

                label = {
                    Text("Notes")
                },

                placeholder = {
                    Text(
                        "Transport information, address, reminders, etc."
                    )
                },

                modifier =
                    Modifier.fillMaxWidth(),

                minLines = 3
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }

        // =====================================================
        // FIXED ACTION BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 10.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Button(
                onClick = onBack,

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    showDeleteConfirmation =
                        true
                },

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Delete")
            }

            Button(
                onClick = {

                    val selectedDate =
                        date ?: return@Button

                    val updatedItem =
                        currentItem.copy(

                            date =
                                selectedDate,

                            time =
                                time,

                            title =
                                title.trim(),

                            type =
                                type.trim(),

                            nights =
                                nightsText
                                    .toIntOrNull(),

                            location =
                                location
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            notes =
                                notes
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            webLink =
                                null,

                            booked =
                                if (createdFromActivity) {
                                    true
                                } else {
                                    booked
                                }
                        )

                    coroutineScope.launch {

                        // =====================================
                        // DELETE REMOVED LINKS
                        // =====================================

                        savedLinks.forEach { savedLink ->

                            if (
                                links.none {
                                    it.id == savedLink.id
                                }
                            ) {

                                tripViewModel
                                    .deleteItineraryLink(
                                        savedLink
                                    )
                            }
                        }

                        // =====================================
                        // ADD NEW / UPDATE EXISTING LINKS
                        // =====================================

                        links.forEach { link ->

                            if (link.id == 0L) {

                                tripViewModel
                                    .addItineraryLink(
                                        ItineraryLinkEntity(
                                            id = 0,
                                            itineraryId =
                                                itineraryId,
                                            label =
                                                link.label,
                                            url =
                                                link.url
                                        )
                                    )

                            } else {

                                val original =
                                    savedLinks.find {
                                        it.id == link.id
                                    }

                                if (
                                    original != null &&
                                    (
                                            original.label !=
                                                    link.label ||
                                                    original.url !=
                                                    link.url
                                            )
                                ) {

                                    tripViewModel
                                        .updateItineraryLink(
                                            ItineraryLinkEntity(
                                                id =
                                                    link.id,
                                                itineraryId =
                                                    itineraryId,
                                                label =
                                                    link.label,
                                                url =
                                                    link.url
                                            )
                                        )
                                }
                            }
                        }

                        // =====================================
                        // UPDATE ITINERARY
                        // =====================================

                        tripViewModel.updateItinerary(
                            updatedItem,
                            selectedDestinationIds
                        ) {
                            onItineraryUpdated()
                        }
                    }
                },

                enabled =
                    date != null &&
                            title.isNotBlank() &&
                            type.isNotBlank() &&
                            destinationsLoaded &&
                            linksLoaded,

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }

    // =========================================================
    // DATE PICKER
    // =========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    date
                        ?.atStartOfDay(
                            ZoneId.systemDefault()
                        )
                        ?.toInstant()
                        ?.toEpochMilli()
            )

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let { millis ->

                                date =
                                    Instant
                                        .ofEpochMilli(
                                            millis
                                        )
                                        .atZone(
                                            ZoneId.systemDefault()
                                        )
                                        .toLocalDate()
                            }

                        showDatePicker =
                            false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showDatePicker =
                            false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state =
                    datePickerState
            )
        }
    }

    // =========================================================
    // TIME PICKER
    // =========================================================

    if (showTimePicker) {

        val timePickerState =
            rememberTimePickerState(
                initialHour =
                    time?.hour ?: 12,

                initialMinute =
                    time?.minute ?: 0
            )

        TimePickerDialog(

            onDismissRequest = {
                showTimePicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        time =
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )

                        showTimePicker =
                            false
                    }
                ) {
                    Text("OK")
                }
            },

            title = {
                Text("Select time")
            }
        ) {

            TimePicker(
                state =
                    timePickerState
            )
        }
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    if (showDeleteConfirmation) {

        AlertDialog(

            onDismissRequest = {
                showDeleteConfirmation =
                    false
            },

            title = {
                Text("Delete Item?")
            },

            text = {
                Text(
                    "Are you sure you want to delete this itinerary item?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteConfirmation =
                            false

                        onDeleteItinerary(
                            currentItem
                        )
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteConfirmation =
                            false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// =============================================================
// WEB LINK VALIDATION
// =============================================================

private fun isValidWebLink(
    link: String
): Boolean {

    if (link.isBlank()) {
        return false
    }

    return try {

        val uri =
            URI(link)

        val scheme =
            uri.scheme?.lowercase()

        !uri.host.isNullOrBlank() &&
                (
                        scheme == "http" ||
                                scheme == "https"
                        )

    } catch (
        exception: URISyntaxException
    ) {

        false
    }
}

// =============================================================
// SAFE WEB LINK OPENING
// =============================================================

private fun openWebLink(
    context: Context,
    link: String
) {

    if (!isValidWebLink(link)) {
        return
    }

    try {

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )

        context.startActivity(intent)

    } catch (
        exception: ActivityNotFoundException
    ) {

        // Deliberately do nothing.
        // The app must never crash because a saved web link
        // cannot be opened.

    } catch (
        exception: SecurityException
    ) {

        // Deliberately do nothing.
    }
}