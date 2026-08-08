package com.wandermore.travelcompanion.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class DateConverter {

    @TypeConverter
    fun fromLocalDate(
        date: LocalDate?
    ): String? {

        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(
        date: String?
    ): LocalDate? {

        return date?.let {
            LocalDate.parse(it)
        }
    }

    @TypeConverter
    fun fromLocalTime(
        time: LocalTime?
    ): String? {

        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(
        time: String?
    ): LocalTime? {

        return time?.let {
            LocalTime.parse(it)
        }
    }
}