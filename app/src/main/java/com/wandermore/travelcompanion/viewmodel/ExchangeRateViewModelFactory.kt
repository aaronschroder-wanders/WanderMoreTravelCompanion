package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository


class ExchangeRateViewModelFactory(

    private val repository: ExchangeRateRepository

) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (modelClass.isAssignableFrom(
                ExchangeRateViewModel::class.java
            )
        ) {


            @Suppress("UNCHECKED_CAST")
            return ExchangeRateViewModel(
                repository
            ) as T


        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}