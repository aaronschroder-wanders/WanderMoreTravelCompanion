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
        ExchangeRateEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun exchangeRateDao(): ExchangeRateDao


    companion object {

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
    }
}