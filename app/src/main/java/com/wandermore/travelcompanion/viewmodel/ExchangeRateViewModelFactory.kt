package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository

class ExchangeRateViewModelFactory(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                ExchangeRateViewModel::class.java
            )
        ) {

            return ExchangeRateViewModel(
                exchangeRateRepository,
                userSettingsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}