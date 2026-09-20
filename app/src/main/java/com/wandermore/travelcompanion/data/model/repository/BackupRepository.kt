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

        // ---------------------------------------------------------
        // ITINERARY LINKS / DOCUMENTS
        // ---------------------------------------------------------

        val itineraryLinks =
            itinerary.flatMap { itineraryItem ->

                database.itineraryLinkDao()
                    .getLinksForItinerary(
                        itineraryItem.id
                    )
                    .first()
            }

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
                backupVersion = 5,

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

                itineraryLinks =
                    itineraryLinks,

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
    // CREATE TRIP EXPENSES CSV
    // ---------------------------------------------------------

    suspend fun createExpensesCsv(
        tripId: Long
    ): String {

        val trip =
            database.tripDao()
                .getAllTripsForBackup()
                .firstOrNull {
                    it.id == tripId
                }
                ?: throw IllegalArgumentException(
                    "Trip not found."
                )

        val expenses =
            database.expenseDao()
                .getExpensesForTrip(tripId)
                .first()

        val csv =
            StringBuilder()

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        csv.append(
            "Trip,Date,Description,Category,Amount,Currency," +
                    "Exchange Rate,Home Currency Amount,Home Currency,Nights"
        )

        csv.append('\n')

        // -----------------------------------------------------
        // EXPENSES
        // -----------------------------------------------------

        expenses.forEach { expense ->

            csv.append(
                csvValue(trip.name)
            )

            csv.append(',')

            csv.append(
                csvValue(expense.date.toString())
            )

            csv.append(',')

            csv.append(
                csvValue(expense.description)
            )

            csv.append(',')

            csv.append(
                csvValue(expense.category)
            )

            csv.append(',')

            csv.append(
                csvValue(expense.amount.toString())
            )

            csv.append(',')

            csv.append(
                csvValue(expense.currency)
            )

            csv.append(',')

            csv.append(
                csvValue(expense.exchangeRate.toString())
            )

            csv.append(',')

            csv.append(
                csvValue(expense.convertedAmount.toString())
            )

            csv.append(',')

            csv.append(
                csvValue(expense.homeCurrency)
            )

            csv.append(',')

            csv.append(
                csvValue(
                    expense.numberOfNights?.toString() ?: ""
                )
            )

            csv.append('\n')
        }

        return csv.toString()
    }

    // ---------------------------------------------------------
    // CREATE ITINERARY CSV
    // ---------------------------------------------------------

    suspend fun createItineraryCsv(
        tripId: Long
    ): String {

        val trip =
            database.tripDao()
                .getAllTripsForBackup()
                .firstOrNull {
                    it.id == tripId
                }
                ?: throw IllegalArgumentException(
                    "Trip not found."
                )

        val itinerary =
            database.itineraryDao()
                .getItineraryForExport(tripId)

        val csv =
            StringBuilder()

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        csv.append(
            "Trip,Date,Time,Title,Type,Nights,Destination,Notes,Booked"
        )

        csv.append('\n')

        // -----------------------------------------------------
        // ITINERARY
        // -----------------------------------------------------

        itinerary.forEach { item ->

            val destinations =
                database.itineraryDestinationDao()
                    .getDestinationsForItinerary(
                        item.id
                    )
                    .joinToString("; ") {
                        it.name
                    }

            csv.append(
                csvValue(trip.name)
            )

            csv.append(',')

            csv.append(
                csvValue(item.date.toString())
            )

            csv.append(',')

            csv.append(
                csvValue(item.time?.toString() ?: "")
            )

            csv.append(',')

            csv.append(
                csvValue(item.title)
            )

            csv.append(',')

            csv.append(
                csvValue(item.type)
            )

            csv.append(',')

            csv.append(
                csvValue(item.nights?.toString() ?: "")
            )

            csv.append(',')

            csv.append(
                csvValue(destinations)
            )

            csv.append(',')

            csv.append(
                csvValue(item.notes ?: "")
            )

            csv.append(',')

            csv.append(
                csvValue(item.booked.toString())
            )

            csv.append('\n')
        }

        return csv.toString()
    }

    // ---------------------------------------------------------
    // CSV VALUE ESCAPING
    // ---------------------------------------------------------
    //
    // Values containing commas, quotation marks or line breaks
    // are enclosed in quotation marks as required by CSV format.
    // ---------------------------------------------------------

    private fun csvValue(
        value: String
    ): String {

        if (
            value.contains(',') ||
            value.contains('"') ||
            value.contains('\n') ||
            value.contains('\r')
        ) {

            return "\"" +
                    value.replace(
                        "\"",
                        "\"\""
                    ) +
                    "\""
        }

        return value
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
            // RESTORE ITINERARY LINKS / DOCUMENTS
            // -------------------------------------------------

            backup.itineraryLinks.forEach { itineraryLink ->

                database.itineraryLinkDao()
                    .insert(itineraryLink)
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