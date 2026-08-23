package com.wandermore.travelcompanion.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(
        context: Context
    ): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wander_more_database"
                )
                    .addMigrations(
                        AppDatabase.MIGRATION_1_2,
                        AppDatabase.MIGRATION_2_3,
                        AppDatabase.MIGRATION_3_4,
                        AppDatabase.MIGRATION_4_5,
                        AppDatabase.MIGRATION_5_6,
                        AppDatabase.MIGRATION_6_7,
                        AppDatabase.MIGRATION_7_8,
                        AppDatabase.MIGRATION_8_9,
                        AppDatabase.MIGRATION_9_10,
                        AppDatabase.MIGRATION_10_11,
                        AppDatabase.MIGRATION_11_12,
                        AppDatabase.MIGRATION_12_13
                    )
                    .build()

            INSTANCE = instance

            instance
        }
    }
}