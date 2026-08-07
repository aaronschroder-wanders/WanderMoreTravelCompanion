package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import com.wandermore.travelcompanion.database.ExchangeRateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ExchangeRateViewModel(

    private val repository: ExchangeRateRepository

) : ViewModel() {



    private val _rates =
        MutableStateFlow<List<ExchangeRateEntity>>(emptyList())


    val rates: StateFlow<List<ExchangeRateEntity>> =
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
                databaseRates.sortedBy {

                    it.currencyCode

                }


        }

    }




    fun getRate(

        currency: String

    ): Double {


        return _rates.value
            .find {

                it.currencyCode == currency

            }
            ?.rateToNZD
            ?: 0.0


    }





    /**
     * Updates a currency rate.
     *
     * Database stores:
     *
     * 1 foreign currency = X NZD
     *
     * UI supplies the traveller-friendly value:
     *
     * 1 NZD = X foreign currency
     *
     * Conversion happens here.
     */
    fun updateRate(

        currencyCode: String,

        inverseRate: Double

    ) {


        viewModelScope.launch {


            if (inverseRate > 0) {


                val rateToNZD =
                    1 / inverseRate



                repository.updateRate(

                    currencyCode,

                    rateToNZD

                )


                loadRates()


            }


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