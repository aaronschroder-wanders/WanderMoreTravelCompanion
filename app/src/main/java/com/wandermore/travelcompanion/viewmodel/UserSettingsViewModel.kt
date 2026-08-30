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

    // ---------------------------------------------------------
    // HOME CURRENCY
    // ---------------------------------------------------------

    val homeCurrency: StateFlow<String> =
        repository.homeCurrency
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = "NZD"
            )


    // ---------------------------------------------------------
    // HOME CURRENCY SETUP STATUS
    //
    // null  = DataStore is still loading
    // false = new/unconfigured installation
    // true  = Home Currency has been configured
    // ---------------------------------------------------------

    val hasHomeCurrencyBeenSet: StateFlow<Boolean?> =
        repository.hasHomeCurrencyBeenSet
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )


    // ---------------------------------------------------------
    // SET HOME CURRENCY
    // ---------------------------------------------------------

    fun setHomeCurrency(
        currency: String,
        onComplete: () -> Unit = {}
    ) {

        viewModelScope.launch {

            repository.setHomeCurrency(
                currency
            )

            onComplete()
        }
    }

}