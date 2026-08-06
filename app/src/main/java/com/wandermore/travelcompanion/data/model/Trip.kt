package com.wandermore.travelcompanion.model

import java.time.LocalDate

data class Trip(
    val id: Long = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val homeCurrency: String,
    val status: TripStatus = TripStatus.PLANNED
)



