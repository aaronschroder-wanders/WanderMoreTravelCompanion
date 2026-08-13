package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository

class UserSettingsViewModelFactory(
    private val repository: UserSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                UserSettingsViewModel::class.java
            )
        ) {

            return UserSettingsViewModel(
                repository
            ) as T

        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}