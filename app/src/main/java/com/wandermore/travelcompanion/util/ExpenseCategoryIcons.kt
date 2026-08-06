package com.wandermore.travelcompanion.util

object ExpenseCategoryIcons {

    fun getIcon(category: String): String {

        return when (category) {

            "Accommodation" -> "🏨"

            "Transport" -> "🚆"

            "Food & Drink" -> "🍜"

            "Attractions" -> "🎟️"

            "Airfares" -> "✈️"

            "Phones" -> "📱"

            "Travel Insurance" -> "🛡️"

            "Shopping" -> "🛍️"

            else -> "📦"

        }

    }

}