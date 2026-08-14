package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository
import com.wandermore.travelcompanion.database.ExchangeRateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ExchangeRateViewModel(

    private val repository: ExchangeRateRepository,

    private val userSettingsRepository: UserSettingsRepository

) : ViewModel() {


    private val _rates =
        MutableStateFlow<List<ExchangeRateEntity>>(emptyList())


    val rates: StateFlow<List<ExchangeRateEntity>> =
        _rates


    private val _homeCurrency =
        MutableStateFlow("NZD")


    val homeCurrency: StateFlow<String> =
        _homeCurrency


    init {

        viewModelScope.launch {

            userSettingsRepository
                .homeCurrency
                .collect { currency ->

                    _homeCurrency.value = currency

                }

        }

    }


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


    /**
     * Returns the exchange rate from the supplied currency
     * to the user's current Home Currency.
     *
     * Database rates are stored as:
     *
     * 1 foreign currency = X NZD
     *
     * Therefore:
     *
     * foreign -> Home Currency
     *
     * is calculated as:
     *
     * foreign rate to NZD / Home Currency rate to NZD
     */
    fun getRate(
        currency: String
    ): Double {

        val home =
            _homeCurrency.value


        // Same currency requires no conversion.
        if (currency == home) {

            return 1.0

        }


        val sourceRate =
            _rates.value
                .find {
                    it.currencyCode == currency
                }
                ?.rateToNZD
                ?: return 0.0


        val homeRate =
            _rates.value
                .find {
                    it.currencyCode == home
                }
                ?.rateToNZD
                ?: return 0.0


        if (homeRate <= 0.0) {

            return 0.0

        }


        return sourceRate / homeRate

    }

    /**
     * Returns the underlying reference exchange rate
     * from the supplied currency to NZD.
     *
     * Database stores:
     *
     * 1 unit of currency = X NZD
     *
     * This rate is independent of the user's Home Currency
     * and is used when storing an expense's historical rate.
     */
    fun getRateToNZD(
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
     * The database stores:
     *
     * 1 foreign currency = X NZD
     *
     * The UI supplies:
     *
     * 1 NZD = X foreign currency
     *
     * so the supplied value is inverted before being
     * stored in the database.
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