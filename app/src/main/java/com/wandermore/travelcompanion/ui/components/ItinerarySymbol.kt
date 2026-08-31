package com.wandermore.travelcompanion.ui.components

fun itinerarySymbol(
    type: String
): String {

    return when (
        type.trim().lowercase()
    ) {

        "travel" ->
            "🚆"

        "accommodation" ->
            "🏨"

        "activity" ->
            "🎯"

        "attraction" ->
            "📸"

        "arrival" ->
            "🛬"

        "departure" ->
            "🛫"

        "other" ->
            "📌"

        else ->
            "📅"
    }
}