package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.TodayTomorrowCard
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate

@Composable
fun TodayTomorrowScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onItineraryClick: (Long) -> Unit,
    onTodoClick: (Long) -> Unit,
    onBack: () -> Unit
) {

// =========================================================
// LOAD TRIP
// =========================================================

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(initial = null)

    val currentTrip =
        tripState ?: return

// =========================================================
// SELECTED DAY
// =========================================================

    var showTomorrow by remember {
        mutableStateOf(false)
    }

    val today =
        LocalDate.now()

    val tomorrow =
        today.plusDays(1)

    val selectedDate =
        if (showTomorrow) {
            tomorrow
        } else {
            today
        }

// =========================================================
// TODAY DATA
// =========================================================

    val todayItinerary by tripViewModel
        .getItineraryForDate(
            tripId,
            today
        )
        .collectAsState(
            initial = emptyList()
        )

    val todayTodos by tripViewModel
        .getTodosForDate(
            tripId,
            today
        )
        .collectAsState(
            initial = emptyList()
        )

// =========================================================
// TOMORROW DATA
// =========================================================

    val tomorrowItinerary by tripViewModel
        .getItineraryForDate(
            tripId,
            tomorrow
        )
        .collectAsState(
            initial = emptyList()
        )

    val tomorrowTodos by tripViewModel
        .getTodosForDate(
            tripId,
            tomorrow
        )
        .collectAsState(
            initial = emptyList()
        )

// =========================================================
// SELECTED DATA
// =========================================================

    val selectedItinerary =
        if (showTomorrow) {
            tomorrowItinerary
        } else {
            todayItinerary
        }

    val selectedTodos =
        if (showTomorrow) {
            tomorrowTodos
        } else {
            todayTodos
        }

// =========================================================
// SCREEN
// =========================================================

    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // =====================================================
        // SCROLLABLE CONTENT
        // =====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =================================================
            // SHARED TRIP SECTION HEADER
            // =================================================

            TripSectionHeader(
                title = "Today / Tomorrow",
                tripName = currentTrip.name,
                startDate = currentTrip.startDate,
                endDate = currentTrip.endDate,
                icon = "📋"
            )

            // =================================================
            // TOGGLE
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp,
                        bottom = 20.dp
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {
                        showTomorrow = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {

                    Text(
                        text = "Today"
                    )
                }

                Button(
                    onClick = {
                        showTomorrow = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {

                    Text(
                        text = "Tomorrow"
                    )
                }
            }

            // =================================================
            // SELECTED DAY
            // =================================================

            TodayTomorrowCard(
                title =
                    if (showTomorrow) {
                        "Tomorrow"
                    } else {
                        "Today"
                    },

                date =
                    selectedDate,

                itineraryItems =
                    selectedItinerary,

                todos =
                    selectedTodos,

                onItineraryClick =
                    onItineraryClick,

                onTodoClick =
                    onTodoClick
            )
        }

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 12.dp
                )
        ) {

            Text(
                text = "Back"
            )
        }
    }

}
