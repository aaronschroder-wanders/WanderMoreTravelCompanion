package com.wandermore.travelcompanion.database

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(

    val backupVersion: Int = 1,

    val createdAt: String,

    val trips: List<TripEntity>,

    val expenses: List<ExpenseEntity>,

    val exchangeRates: List<ExchangeRateEntity>,

    val todos: List<TodoEntity>,

    val activities: List<ActivityEntity>,

    val itinerary: List<ItineraryEntity>,

    val bookings: List<BookingEntity>,

    val tripEstimates: List<TripEstimateEntity>
)