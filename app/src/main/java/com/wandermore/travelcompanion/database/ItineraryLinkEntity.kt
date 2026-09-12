package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "itinerary_links",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["itineraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["itineraryId"])
    ]
)
data class ItineraryLinkEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val itineraryId: Long,

    val label: String,

    val url: String
)