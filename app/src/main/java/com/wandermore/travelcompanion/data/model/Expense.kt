package com.wandermore.travelcompanion.data.model

import java.time.LocalDate

data class Expense(

    val id: Int = 0,

    val tripId: Long,

    val description: String,

    val amount: Double,

    val currency: String,

    val date: LocalDate,

    val category: String

)