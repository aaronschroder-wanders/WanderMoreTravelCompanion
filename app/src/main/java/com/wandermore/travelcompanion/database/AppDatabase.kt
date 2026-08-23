package com.wandermore.travelcompanion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TripEntity::class,
        ExpenseEntity::class,
        ExchangeRateEntity::class,
        TodoEntity::class,
        ActivityEntity::class,
        ItineraryEntity::class,
        BookingEntity::class,
        TripEstimateEntity::class,
        DestinationEntity::class,
        TripDestinationEntity::class,
        ItineraryDestinationEntity::class,
        ActivityDestinationEntity::class
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun exchangeRateDao(): ExchangeRateDao

    abstract fun todoDao(): TodoDao

    abstract fun activityDao(): ActivityDao

    abstract fun itineraryDao(): ItineraryDao

    abstract fun tripEstimateDao(): TripEstimateDao

    abstract fun bookingDao(): BookingDao

    abstract fun destinationDao(): DestinationDao

    abstract fun tripDestinationDao(): TripDestinationDao

    abstract fun itineraryDestinationDao(): ItineraryDestinationDao

    abstract fun activityDestinationDao(): ActivityDestinationDao


    companion object {

        // ---------------------------------------------------------
        // VERSION 1 → 2
        // EXPENSE EXCHANGE RATE
        // ---------------------------------------------------------

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE expenses
                    ADD COLUMN exchangeRate REAL NOT NULL DEFAULT 1.0
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE expenses
                    ADD COLUMN convertedAmount REAL NOT NULL DEFAULT 0.0
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 2 → 3
        // ACCOMMODATION NIGHTS
        // ---------------------------------------------------------

        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE expenses
                    ADD COLUMN numberOfNights INTEGER
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 3 → 4
        // EXCHANGE RATES TABLE
        // ---------------------------------------------------------

        val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS exchange_rates (
                        currencyCode TEXT NOT NULL,
                        rateToNZD REAL NOT NULL,
                        lastUpdated TEXT NOT NULL,
                        PRIMARY KEY(currencyCode)
                    )
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 4 → 5
        // TRIP STATUS
        // ---------------------------------------------------------

        val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE trips
                    ADD COLUMN status TEXT NOT NULL DEFAULT 'PLANNED'
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    UPDATE trips
                    SET status = 'CURRENT'
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 5 → 6
        // TO DOS
        // ---------------------------------------------------------

        val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        task TEXT NOT NULL,
                        dueDate TEXT,
                        assignedTo TEXT NOT NULL,
                        completed INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_todos_tripId
                    ON todos(tripId)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 6 → 7
        // ACTIVITIES
        // ---------------------------------------------------------

        val MIGRATION_6_7 = object : Migration(6, 7) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS activities (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        location TEXT,
                        estimatedCost REAL,
                        currency TEXT,
                        convertedAmount REAL,
                        booked INTEGER NOT NULL DEFAULT 0,
                        website TEXT,
                        notes TEXT,
                        date TEXT,
                        startTime TEXT,
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_activities_tripId
                    ON activities(tripId)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 7 → 8
        // ITINERARY + BOOKINGS
        // ---------------------------------------------------------

        val MIGRATION_7_8 = object : Migration(7, 8) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                // -------------------------------------------------
                // ITINERARY
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS itinerary (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT,
                        title TEXT NOT NULL,
                        type TEXT NOT NULL,
                        nights INTEGER,
                        location TEXT,
                        notes TEXT,
                        bookingId INTEGER,
                        sortOrder INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_itinerary_tripId
                    ON itinerary(tripId)
                    """.trimIndent()
                )


                // -------------------------------------------------
                // BOOKINGS
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS bookings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        date TEXT,
                        time TEXT,
                        provider TEXT,
                        reference TEXT,
                        cost REAL,
                        currency TEXT,
                        convertedAmount REAL,
                        cancelFreeBefore TEXT,
                        address TEXT,
                        website TEXT,
                        notes TEXT,
                        status TEXT NOT NULL DEFAULT 'PLANNED',
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_bookings_tripId
                    ON bookings(tripId)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 8 → 9
        // LINK ACTIVITIES TO ITINERARY
        // ---------------------------------------------------------

        val MIGRATION_8_9 = object : Migration(8, 9) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE itinerary
                    ADD COLUMN activityId INTEGER
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE itinerary
                    ADD COLUMN booked INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_itinerary_activityId
                    ON itinerary(activityId)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 9 → 10
        // TRIP ESTIMATES
        // ---------------------------------------------------------

        val MIGRATION_9_10 = object : Migration(9, 10) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS trip_estimates (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        estimateType TEXT NOT NULL,
                        amount REAL NOT NULL,
                        currency TEXT NOT NULL,
                        convertedAmount REAL NOT NULL,
                        notes TEXT,
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_trip_estimates_tripId
                    ON trip_estimates(tripId)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_trip_estimates_tripId_category
                    ON trip_estimates(tripId, category)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 10 → 11
        // EXPENSE SCHEMA UPDATE
        //
        // ExpenseEntity now expects:
        //
        // - homeCurrency TEXT NOT NULL
        // - exchangeRate REAL NOT NULL
        // - convertedAmount REAL NOT NULL
        //
        // The previous schema had SQLite DEFAULT values on
        // exchangeRate and convertedAmount and did not have
        // homeCurrency.
        //
        // SQLite ALTER TABLE cannot remove those defaults, so
        // the expenses table must be rebuilt.
        // ---------------------------------------------------------

        val MIGRATION_10_11 = object : Migration(10, 11) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                // -------------------------------------------------
                // Create the new expenses table with the exact
                // schema expected by Room / ExpenseEntity.
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE expenses_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        category TEXT NOT NULL,
                        description TEXT NOT NULL,
                        amount REAL NOT NULL,
                        currency TEXT NOT NULL,
                        exchangeRate REAL NOT NULL,
                        convertedAmount REAL NOT NULL,
                        homeCurrency TEXT NOT NULL,
                        numberOfNights INTEGER,
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )


                // -------------------------------------------------
                // Copy existing expenses.
                //
                // Existing expenses were stored using NZD as the
                // application's home currency, so existing rows
                // are migrated with homeCurrency = NZD.
                //
                // The existing exchangeRate and convertedAmount
                // values are retained.
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT INTO expenses_new (
                        id,
                        tripId,
                        date,
                        category,
                        description,
                        amount,
                        currency,
                        exchangeRate,
                        convertedAmount,
                        homeCurrency,
                        numberOfNights
                    )
                    SELECT
                        id,
                        tripId,
                        date,
                        category,
                        description,
                        amount,
                        currency,
                        exchangeRate,
                        convertedAmount,
                        'NZD',
                        numberOfNights
                    FROM expenses
                    """.trimIndent()
                )


                // -------------------------------------------------
                // Remove the old table.
                // -------------------------------------------------

                database.execSQL(
                    """
                    DROP TABLE expenses
                    """.trimIndent()
                )


                // -------------------------------------------------
                // Rename the new table.
                // -------------------------------------------------

                database.execSQL(
                    """
                    ALTER TABLE expenses_new
                    RENAME TO expenses
                    """.trimIndent()
                )


                // -------------------------------------------------
                // Recreate the Room-generated index.
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_expenses_tripId
                    ON expenses(tripId)
                    """.trimIndent()
                )
            }
        }


        // ---------------------------------------------------------
        // VERSION 11 → 12
        // DESTINATIONS
        //
        // Introduces reusable Destinations and many-to-many
        // relationships with Trips, Itinerary items and Activities.
        //
        // Existing Location values are migrated into Destinations.
        // ---------------------------------------------------------

        val MIGRATION_11_12 = object : Migration(11, 12) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                // -------------------------------------------------
                // DESTINATIONS
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS destinations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_destinations_name
                    ON destinations(name COLLATE NOCASE)
                    """.trimIndent()
                )


                // -------------------------------------------------
                // TRIP ↔ DESTINATION
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS trip_destinations (
                        tripId INTEGER NOT NULL,
                        destinationId INTEGER NOT NULL,
                        PRIMARY KEY(tripId, destinationId),
                        FOREIGN KEY(tripId)
                            REFERENCES trips(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY(destinationId)
                            REFERENCES destinations(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_trip_destinations_tripId
                    ON trip_destinations(tripId)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_trip_destinations_destinationId
                    ON trip_destinations(destinationId)
                    """.trimIndent()
                )


                // -------------------------------------------------
                // ITINERARY ↔ DESTINATION
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS itinerary_destinations (
                        itineraryId INTEGER NOT NULL,
                        destinationId INTEGER NOT NULL,
                        PRIMARY KEY(itineraryId, destinationId),
                        FOREIGN KEY(itineraryId)
                            REFERENCES itinerary(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY(destinationId)
                            REFERENCES destinations(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_itinerary_destinations_itineraryId
                    ON itinerary_destinations(itineraryId)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_itinerary_destinations_destinationId
                    ON itinerary_destinations(destinationId)
                    """.trimIndent()
                )


                // -------------------------------------------------
                // ACTIVITY ↔ DESTINATION
                // -------------------------------------------------

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS activity_destinations (
                        activityId INTEGER NOT NULL,
                        destinationId INTEGER NOT NULL,
                        PRIMARY KEY(activityId, destinationId),
                        FOREIGN KEY(activityId)
                            REFERENCES activities(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY(destinationId)
                            REFERENCES destinations(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_activity_destinations_activityId
                    ON activity_destinations(activityId)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_activity_destinations_destinationId
                    ON activity_destinations(destinationId)
                    """.trimIndent()
                )


                // -------------------------------------------------
                // MIGRATE EXISTING ITINERARY LOCATIONS
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO destinations (name)
                    SELECT DISTINCT TRIM(location)
                    FROM itinerary
                    WHERE location IS NOT NULL
                      AND TRIM(location) != ''
                    """.trimIndent()
                )


                // -------------------------------------------------
                // MIGRATE EXISTING ACTIVITY LOCATIONS
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO destinations (name)
                    SELECT DISTINCT TRIM(location)
                    FROM activities
                    WHERE location IS NOT NULL
                      AND TRIM(location) != ''
                    """.trimIndent()
                )


                // -------------------------------------------------
                // LINK EXISTING ITINERARY LOCATIONS
                // TO DESTINATIONS
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO itinerary_destinations (
                        itineraryId,
                        destinationId
                    )
                    SELECT
                        i.id,
                        d.id
                    FROM itinerary i
                    INNER JOIN destinations d
                        ON TRIM(i.location) = d.name COLLATE NOCASE
                    WHERE i.location IS NOT NULL
                      AND TRIM(i.location) != ''
                    """.trimIndent()
                )


                // -------------------------------------------------
                // LINK EXISTING ACTIVITY LOCATIONS
                // TO DESTINATIONS
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO activity_destinations (
                        activityId,
                        destinationId
                    )
                    SELECT
                        a.id,
                        d.id
                    FROM activities a
                    INNER JOIN destinations d
                        ON TRIM(a.location) = d.name COLLATE NOCASE
                    WHERE a.location IS NOT NULL
                      AND TRIM(a.location) != ''
                    """.trimIndent()
                )


                // -------------------------------------------------
                // LINK DESTINATIONS TO THEIR TRIPS
                //
                // A destination becomes part of a trip if it is
                // associated with an itinerary item or activity
                // belonging to that trip.
                // -------------------------------------------------

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO trip_destinations (
                        tripId,
                        destinationId
                    )
                    SELECT DISTINCT
                        i.tripId,
                        id.destinationId
                    FROM itinerary i
                    INNER JOIN itinerary_destinations id
                        ON i.id = id.itineraryId
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT OR IGNORE INTO trip_destinations (
                        tripId,
                        destinationId
                    )
                    SELECT DISTINCT
                        a.tripId,
                        ad.destinationId
                    FROM activities a
                    INNER JOIN activity_destinations ad
                        ON a.id = ad.activityId
                    """.trimIndent()
                )
            }
        }

        // ---------------------------------------------------------
// VERSION 12 → 13
// DESTINATION ACTIVE / ARCHIVED STATUS
//
// Destinations are global and reusable across trips.
// The active flag allows destinations to be temporarily
// archived without deleting them.
// ---------------------------------------------------------

        val MIGRATION_12_13 = object : Migration(12, 13) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
            ALTER TABLE destinations
            ADD COLUMN active INTEGER NOT NULL DEFAULT 1
            """.trimIndent()
                )
            }
        }
    }
}