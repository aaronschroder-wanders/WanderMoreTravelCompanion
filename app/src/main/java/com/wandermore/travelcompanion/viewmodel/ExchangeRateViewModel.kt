package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import kotlinx.coroutines.launch


class ExchangeRateViewModel(

    private val repository: ExchangeRateRepository

) : ViewModel() {


    fun initialiseRates() {

        viewModelScope.launch {

            repository.insertInitialRates()

        }

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