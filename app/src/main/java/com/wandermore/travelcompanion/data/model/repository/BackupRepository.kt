package com.wandermore.travelcompanion.data.repository

import androidx.room.withTransaction
import com.wandermore.travelcompanion.database.AppDatabase
import com.wandermore.travelcompanion.database.BackupData
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class BackupRepository(
    private val database: AppDatabase,
    private val userSettingsRepository: UserSettingsRepository
) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    // ---------------------------------------------------------
    // CREATE BACKUP
    // ---------------------------------------------------------

    suspend fun createBackup(): String {

        val trips =
            database.tripDao()
                .getAllTripsForBackup()

        val expenses =
            database.expenseDao()
                .getAllExpensesForBackup()

        val exchangeRates =
            database.exchangeRateDao()
                .getAllRates()

        val todos =
            database.todoDao()
                .getAllTodosForBackup()

        val activities =
            database.activityDao()
                .getAllActivitiesForBackup()

        val itinerary =
            database.itineraryDao()
                .getAllItineraryForBackup()

        val bookings =
            database.bookingDao()
                .getAllBookingsForBackup()

        val tripEstimates =
            database.tripEstimateDao()
                .getAllEstimatesForBackup()

        // ---------------------------------------------------------
        // DESTINATIONS
        // ---------------------------------------------------------

        val destinations =
            database.destinationDao()
                .getAllDestinations()
                .first()

        val tripDestinations =
            trips.flatMap { trip ->
                database.tripDestinationDao()
                    .getDestinationIdsForTrip(trip.id)
                    .map { destinationId ->
                        com.wandermore.travelcompanion.database.TripDestinationEntity(
                            tripId = trip.id,
                            destinationId = destinationId
                        )
                    }
            }

        val itineraryDestinations =
            itinerary.flatMap { itineraryItem ->
                database.itineraryDestinationDao()
                    .getDestinationIdsForItinerary(itineraryItem.id)
                    .map { destinationId ->
                        com.wandermore.travelcompanion.database.ItineraryDestinationEntity(
                            itineraryId = itineraryItem.id,
                            destinationId = destinationId
                        )
                    }
            }

        val activityDestinations =
            activities.flatMap { activity ->
                database.activityDestinationDao()
                    .getDestinationIdsForActivity(activity.id)
                    .map { destinationId ->
                        com.wandermore.travelcompanion.database.ActivityDestinationEntity(
                            activityId = activity.id,
                            destinationId = destinationId
                        )
                    }
            }

        val homeCurrency =
            userSettingsRepository
                .homeCurrency
                .first()

        val backup =
            BackupData(
                backupVersion = 2,
                createdAt = LocalDateTime.now().toString(),

                homeCurrency = homeCurrency,

                trips = trips,
                expenses = expenses,
                exchangeRates = exchangeRates,
                todos = todos,
                activities = activities,
                itinerary = itinerary,
                bookings = bookings,
                tripEstimates = tripEstimates,

                destinations = destinations,
                tripDestinations = tripDestinations,
                itineraryDestinations = itineraryDestinations,
                activityDestinations = activityDestinations
            )

        return json.encodeToString(
            BackupData.serializer(),
            backup
        )
    }


    // ---------------------------------------------------------
    // RESTORE BACKUP
    // ---------------------------------------------------------

    suspend fun restoreBackup(
        backupJson: String
    ) {

        val backup =
            json.decodeFromString(
                BackupData.serializer(),
                backupJson
            )

        database.withTransaction {

            // -------------------------------------------------
            // CLEAR DESTINATION RELATIONSHIPS FIRST
            //
            // These must be removed before the destinations
            // themselves are deleted.
            // -------------------------------------------------

            database.tripDestinationDao()
                .deleteAllTripDestinations()

            database.itineraryDestinationDao()
                .deleteAllItineraryDestinations()

            database.activityDestinationDao()
                .deleteAllActivityDestinations()


            // -------------------------------------------------
            // CLEAR EXISTING DESTINATIONS
            //
            // This prevents UNIQUE constraint errors when the
            // backup contains destination IDs already present
            // on this device.
            // -------------------------------------------------

            database.destinationDao()
                .deleteAllDestinations()


            // -------------------------------------------------
            // DELETE CURRENT TRIPS
            //
            // Child records are deleted automatically because
            // the database relationships use ON DELETE CASCADE.
            // -------------------------------------------------

            database.tripDao()
                .deleteAllTrips()


            // -------------------------------------------------
            // RESTORE TRIPS FIRST
            //
            // The original IDs are preserved so that all child
            // records continue to point to the correct trip.
            // -------------------------------------------------

            backup.trips.forEach { trip ->

                database.tripDao()
                    .insertTrip(trip)
            }


            // -------------------------------------------------
            // RESTORE EXPENSES
            // -------------------------------------------------

            backup.expenses.forEach { expense ->

                database.expenseDao()
                    .insertExpense(expense)
            }


            // -------------------------------------------------
            // RESTORE TO-DOS
            // -------------------------------------------------

            backup.todos.forEach { todo ->

                database.todoDao()
                    .insertTodo(todo)
            }


            // -------------------------------------------------
            // RESTORE ACTIVITIES
            // -------------------------------------------------

            backup.activities.forEach { activity ->

                database.activityDao()
                    .insertActivity(activity)
            }


            // -------------------------------------------------
            // RESTORE ITINERARY
            // -------------------------------------------------

            backup.itinerary.forEach { itineraryItem ->

                database.itineraryDao()
                    .insertItinerary(itineraryItem)
            }


            // -------------------------------------------------
            // RESTORE BOOKINGS
            // -------------------------------------------------

            backup.bookings.forEach { booking ->

                database.bookingDao()
                    .insertBooking(booking)
            }


            // -------------------------------------------------
            // RESTORE TRIP ESTIMATES
            // -------------------------------------------------

            backup.tripEstimates.forEach { estimate ->

                database.tripEstimateDao()
                    .insertEstimate(estimate)
            }


            // -------------------------------------------------
            // RESTORE DESTINATIONS
            // -------------------------------------------------

            backup.destinations.forEach { destination ->

                database.destinationDao()
                    .insertDestination(destination)
            }


            // -------------------------------------------------
            // RESTORE TRIP ↔ DESTINATION RELATIONSHIPS
            // -------------------------------------------------

            backup.tripDestinations.forEach { tripDestination ->

                database.tripDestinationDao()
                    .insertTripDestination(tripDestination)
            }


            // -------------------------------------------------
            // RESTORE ITINERARY ↔ DESTINATION RELATIONSHIPS
            // -------------------------------------------------

            backup.itineraryDestinations.forEach { itineraryDestination ->

                database.itineraryDestinationDao()
                    .insertItineraryDestination(
                        itineraryDestination
                    )
            }


            // -------------------------------------------------
            // RESTORE ACTIVITY ↔ DESTINATION RELATIONSHIPS
            // -------------------------------------------------

            backup.activityDestinations.forEach { activityDestination ->

                database.activityDestinationDao()
                    .insertActivityDestination(
                        activityDestination
                    )
            }


            // -------------------------------------------------
            // RESTORE EXCHANGE RATES
            // -------------------------------------------------

            database.exchangeRateDao()
                .deleteAllRates()

            database.exchangeRateDao()
                .insertRates(
                    backup.exchangeRates
                )
        }

        // ---------------------------------------------------------
        // RESTORE USER SETTINGS
        // ---------------------------------------------------------

        userSettingsRepository
            .setHomeCurrency(
                backup.homeCurrency
            )
    }
}