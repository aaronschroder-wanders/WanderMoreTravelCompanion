package com.wandermore.travelcompanion.data.model

import java.time.LocalDate

data class Trip(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val homeCurrency: String
)



