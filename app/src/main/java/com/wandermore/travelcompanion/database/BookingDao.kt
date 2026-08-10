package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {

    // ---------------------------------------------------------
    // BACKUP
    // ---------------------------------------------------------

    @Query("SELECT * FROM bookings")
    suspend fun getAllBookingsForBackup(): List<BookingEntity>


    // ---------------------------------------------------------
    // RESTORE
    // ---------------------------------------------------------

    @Insert
    suspend fun insertBooking(
        booking: BookingEntity
    )
}