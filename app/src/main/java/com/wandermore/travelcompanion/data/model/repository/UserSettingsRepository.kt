package com.wandermore.travelcompanion.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userSettingsDataStore by preferencesDataStore(
    name = "user_settings"
)

class UserSettingsRepository(
    private val context: Context
) {

    companion object {

        private val HOME_CURRENCY =
            stringPreferencesKey("home_currency")

    }

    val homeCurrency: Flow<String> =
        context.userSettingsDataStore.data
            .map { preferences ->

                preferences[HOME_CURRENCY]
                    ?: "NZD"

            }

    suspend fun setHomeCurrency(
        currency: String
    ) {

        context.userSettingsDataStore.edit { preferences ->

            preferences[HOME_CURRENCY] =
                currency

        }

    }

}