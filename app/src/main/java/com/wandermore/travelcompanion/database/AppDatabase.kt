package com.wandermore.travelcompanion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TripEntity::class,
        ExpenseEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    abstract fun expenseDao(): ExpenseDao


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
    }
}