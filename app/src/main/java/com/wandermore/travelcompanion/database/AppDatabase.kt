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
        BookingEntity::class
    ],
    version = 9,
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
    }
}