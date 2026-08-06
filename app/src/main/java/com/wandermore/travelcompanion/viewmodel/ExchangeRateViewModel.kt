package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ExchangeRateViewModel(

    private val repository: ExchangeRateRepository

) : ViewModel() {


    private val _rates =
        MutableStateFlow<Map<String, Double>>(emptyMap())


    val rates: StateFlow<Map<String, Double>> =
        _rates



    fun initialiseRates() {

        viewModelScope.launch {

            repository.insertInitialRates()

            loadRates()

        }

    }



    fun loadRates() {

        viewModelScope.launch {

            val databaseRates =
                repository.getAllRates()


            _rates.value =
                databaseRates.associate {

                    it.currencyCode to it.rateToNZD

                }

        }

    }



    fun getRate(
        currency: String
    ): Double {

        return _rates.value[currency]
            ?: 0.0

    }



    fun checkRates() {

        viewModelScope.launch {

            val rates =
                repository.getAllRates()


            rates.forEach {

                println(
                    "RATE CHECK: ${it.currencyCode} = ${it.rateToNZD}"
                )

            }

        }

    }

}