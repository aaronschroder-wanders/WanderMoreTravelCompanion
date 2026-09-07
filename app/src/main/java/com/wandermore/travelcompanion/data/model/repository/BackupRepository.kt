package com.wandermore.travelcompanion.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.wandermore.travelcompanion.database.AppDatabase
import com.wandermore.travelcompanion.database.BackupData
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class BackupRepository(
    private val database: AppDatabase,
    private val userSettingsRepository: UserSettingsRepository,
    private val context: Context
) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    // ---------------------------------------------------------
    // USER LINK PREFERENCES
    // ---------------------------------------------------------

    private val userLinkPreferences =
        context.getSharedPreferences(
            "user_link_preferences",
            Context.MODE_PRIVATE
        )

    // ---------------------------------------------------------
    // MIGRATE OLD DOCUMENT TEST PREFERENCES
    //
    // This preserves the passport links already saved during
    // testing before we renamed the preferences file.
    // ---------------------------------------------------------

    private fun migrateOldDocumentPreferences() {

        val oldPreferences =
            context.getSharedPreferences(
                "document_test_preferences",
                Context.MODE_PRIVATE
            )

        val existingPrimaryLink =
            userLinkPreferences.getString(
                "primary_user_passport_link",
                null
            )

        val existingPartnerLink =
            userLinkPreferences.getString(
                "travel_partner_passport_link",
                null
            )

        val oldPrimaryLink =
            oldPreferences.getString(
                "primary_user_passport_link",
                null
            )

        val oldPartnerLink =
            oldPreferences.getString(
                "travel_partner_passport_link",
                null
            )

        val oldPassportUri =
            oldPreferences.getString(
                "passport_aaron_uri",
                null
            )

        val editor =
            userLinkPreferences.edit()

        // -----------------------------------------------------
        // Primary User
        //
        // Prefer the already-migrated normal web link.
        // If that doesn't exist, use the old primary link.
        // Only migrate passport_aaron_uri if it is actually
        // a normal HTTP/HTTPS web link.
        // -----------------------------------------------------

        if (existingPrimaryLink.isNullOrBlank()) {

            when {

                !oldPrimaryLink.isNullOrBlank() -> {

                    editor.putString(
                        "primary_user_passport_link",
                        oldPrimaryLink
                    )
                }

                oldPassportUri?.startsWith(
                    "http://",
                    ignoreCase = true
                ) == true -> {

                    editor.putString(
                        "primary_user_passport_link",
                        oldPassportUri
                    )
                }

                oldPassportUri?.startsWith(
                    "https://",
                    ignoreCase = true
                ) == true -> {

                    editor.putString(
                        "primary_user_passport_link",
                        oldPassportUri
                    )
                }
            }
        }

        // -----------------------------------------------------
        // Travel Partner
        // -----------------------------------------------------

        if (
            existingPartnerLink.isNullOrBlank() &&
            !oldPartnerLink.isNullOrBlank()
        ) {

            editor.putString(
                "travel_partner_passport_link",
                oldPartnerLink
            )
        }

        editor.apply()
    }

    // ---------------------------------------------------------
    // CREATE BACKUP
    // ---------------------------------------------------------

    suspend fun createBackup(): String {

        migrateOldDocumentPreferences()

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
                    .getDestinationIdsForItinerary(
                        itineraryItem.id
                    )
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
                    .getDestinationIdsForActivity(
                        activity.id
                    )
                    .map { destinationId ->

                        com.wandermore.travelcompanion.database.ActivityDestinationEntity(
                            activityId = activity.id,
                            destinationId = destinationId
                        )
                    }
            }

        // ---------------------------------------------------------
        // HOME CURRENCY
        // ---------------------------------------------------------

        val homeCurrency =
            userSettingsRepository
                .homeCurrency
                .first()

        // ---------------------------------------------------------
        // PASSPORT DOCUMENT LINKS
        // ---------------------------------------------------------

        val primaryUserPassportLink =
            userLinkPreferences.getString(
                "primary_user_passport_link",
                null
            )

        val travelPartnerPassportLink =
            userLinkPreferences.getString(
                "travel_partner_passport_link",
                null
            )

        // ---------------------------------------------------------
        // BUILD BACKUP
        // ---------------------------------------------------------

        val backup =
            BackupData(
                backupVersion = 4,

                createdAt =
                    LocalDateTime.now().toString(),

                homeCurrency =
                    homeCurrency,

                trips =
                    trips,

                expenses =
                    expenses,

                exchangeRates =
                    exchangeRates,

                todos =
                    todos,

                activities =
                    activities,

                itinerary =
                    itinerary,

                bookings =
                    bookings,

                tripEstimates =
                    tripEstimates,

                destinations =
                    destinations,

                tripDestinations =
                    tripDestinations,

                itineraryDestinations =
                    itineraryDestinations,

                activityDestinations =
                    activityDestinations,

                primaryUserPassportLink =
                    primaryUserPassportLink,

                travelPartnerPassportLink =
                    travelPartnerPassportLink
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
            // -------------------------------------------------

            database.tripDestinationDao()
                .deleteAllTripDestinations()

            database.itineraryDestinationDao()
                .deleteAllItineraryDestinations()

            database.activityDestinationDao()
                .deleteAllActivityDestinations()

            // -------------------------------------------------
            // CLEAR EXISTING DESTINATIONS
            // -------------------------------------------------

            database.destinationDao()
                .deleteAllDestinations()

            // -------------------------------------------------
            // DELETE CURRENT TRIPS
            // -------------------------------------------------

            database.tripDao()
                .deleteAllTrips()

            // -------------------------------------------------
            // RESTORE TRIPS
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
                    .insertTripDestination(
                        tripDestination
                    )
            }

            // -------------------------------------------------
            // RESTORE ITINERARY ↔ DESTINATION RELATIONSHIPS
            // -------------------------------------------------

            backup.itineraryDestinations.forEach {
                    itineraryDestination ->

                database.itineraryDestinationDao()
                    .insertItineraryDestination(
                        itineraryDestination
                    )
            }

            // -------------------------------------------------
            // RESTORE ACTIVITY ↔ DESTINATION RELATIONSHIPS
            // -------------------------------------------------

            backup.activityDestinations.forEach {
                    activityDestination ->

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

        // ---------------------------------------------------------
        // RESTORE PASSPORT DOCUMENT LINKS
        // ---------------------------------------------------------

        val editor =
            userLinkPreferences.edit()

        if (
            backup.primaryUserPassportLink
                .isNullOrBlank()
        ) {

            editor.remove(
                "primary_user_passport_link"
            )

        } else {

            editor.putString(
                "primary_user_passport_link",
                backup.primaryUserPassportLink
            )
        }

        if (
            backup.travelPartnerPassportLink
                .isNullOrBlank()
        ) {

            editor.remove(
                "travel_partner_passport_link"
            )

        } else {

            editor.putString(
                "travel_partner_passport_link",
                backup.travelPartnerPassportLink
            )
        }

        editor.apply()
    }
}