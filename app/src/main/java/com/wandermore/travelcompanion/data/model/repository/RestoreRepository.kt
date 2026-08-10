package com.wandermore.travelcompanion.data.repository

import androidx.room.withTransaction
import com.wandermore.travelcompanion.database.AppDatabase
import com.wandermore.travelcompanion.database.BackupData
import kotlinx.serialization.json.Json

class RestoreRepository(
    private val database: AppDatabase
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun restoreBackup(
        backupJson: String
    ) {

        // Convert the JSON backup into our BackupData object
        val backup =
            json.decodeFromString(
                BackupData.serializer(),
                backupJson
            )

        // ---------------------------------------------------------
        // RESTORE EVERYTHING AS ONE DATABASE TRANSACTION
        // ---------------------------------------------------------

        database.withTransaction {

            // -----------------------------------------------------
            // CLEAR EXISTING DATA
            // -----------------------------------------------------

            // Deleting trips also deletes all trip-related data
            // because our Room foreign keys use ON DELETE CASCADE.
            database.tripDao()
                .deleteAllTrips()

            // Exchange rates are not linked to trips,
            // so they must be cleared separately.
            database.exchangeRateDao()
                .deleteAllRates()


            // -----------------------------------------------------
            // RESTORE EXCHANGE RATES
            // -----------------------------------------------------

            database.exchangeRateDao()
                .insertRates(backup.exchangeRates)


            // -----------------------------------------------------
            // RESTORE TRIPS
            // -----------------------------------------------------

            backup.trips.forEach { trip ->
                database.tripDao()
                    .insertTrip(trip)
            }


            // -----------------------------------------------------
            // RESTORE EXPENSES
            // -----------------------------------------------------

            backup.expenses.forEach { expense ->
                database.expenseDao()
                    .insertExpense(expense)
            }


            // -----------------------------------------------------
            // RESTORE TO-DOS
            // -----------------------------------------------------

            backup.todos.forEach { todo ->
                database.todoDao()
                    .insertTodo(todo)
            }


            // -----------------------------------------------------
            // RESTORE ACTIVITIES
            // -----------------------------------------------------

            backup.activities.forEach { activity ->
                database.activityDao()
                    .insertActivity(activity)
            }


            // -----------------------------------------------------
            // RESTORE ITINERARY
            // -----------------------------------------------------

            backup.itinerary.forEach { itinerary ->
                database.itineraryDao()
                    .insertItinerary(itinerary)
            }


            // -----------------------------------------------------
            // RESTORE BOOKINGS
            // -----------------------------------------------------

            backup.bookings.forEach { booking ->
                database.bookingDao()
                    .insertBooking(booking)
            }


            // -----------------------------------------------------
            // RESTORE TRIP ESTIMATES
            // -----------------------------------------------------

            backup.tripEstimates.forEach { estimate ->
                database.tripEstimateDao()
                    .insertEstimate(estimate)
            }
        }
    }
}