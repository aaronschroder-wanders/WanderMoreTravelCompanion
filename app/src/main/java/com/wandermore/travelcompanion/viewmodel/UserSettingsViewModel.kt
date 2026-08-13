package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserSettingsViewModel(
    private val repository: UserSettingsRepository
) : ViewModel() {

    val homeCurrency: StateFlow<String> =
        repository.homeCurrency
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = "NZD"
            )

    fun setHomeCurrency(
        currency: String
    ) {

        viewModelScope.launch {

            repository.setHomeCurrency(
                currency
            )

        }

    }

}