package com.wandermore.travelcompanion.database

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(

    val backupVersion: Int = 2,

    val createdAt: String,

    val homeCurrency: String,

    val trips: List<TripEntity>,

    val expenses: List<ExpenseEntity>,

    val exchangeRates: List<ExchangeRateEntity>,

    val todos: List<TodoEntity>,

    val activities: List<ActivityEntity>,

    val itinerary: List<ItineraryEntity>,

    val bookings: List<BookingEntity>,

    val tripEstimates: List<TripEstimateEntity>,

    // ---------------------------------------------------------
    // DESTINATIONS
    // ---------------------------------------------------------

    val destinations: List<DestinationEntity> = emptyList(),

    val tripDestinations: List<TripDestinationEntity> = emptyList(),

    val itineraryDestinations: List<ItineraryDestinationEntity> = emptyList(),

    val activityDestinations: List<ActivityDestinationEntity> = emptyList()
)