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


    /**
     * Initialises the exchange-rate data.
     *
     * If the database does not contain any rates, the repository
     * will retrieve current rates from the currency service.
     *
     * If rates already exist, they are loaded from Room so the
     * application remains immediately usable offline.
     */
    fun initialiseRates() {

        viewModelScope.launch {

            repository.insertInitialRates()

            loadRates()

        }

    }


    /**
     * Retrieves current exchange rates from the currency service.
     *
     * If the service is unavailable, existing Room rates are
     * retained and the application continues to work offline.
     */
    fun refreshRates() {

        viewModelScope.launch {

            repository.refreshRates()

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
     * 1 currency = X NZD
     *
     * Therefore:
     *
     * source -> Home Currency
     *
     * is calculated as:
     *
     * source rate to NZD / Home Currency rate to NZD
     *
     * This is used by the expense conversion system.
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
     * Returns the exchange rate from the user's
     * Home Currency to the supplied currency.
     *
     * Database rates are stored as:
     *
     * 1 currency = X NZD
     *
     * Therefore:
     *
     * Home Currency -> target currency
     *
     * is calculated as:
     *
     * Home Currency rate to NZD / target rate to NZD
     *
     * This is used for display and editing in the
     * Currency Exchange Rates screen.
     */
    fun getRateFromHomeCurrency(
        currency: String
    ): Double {

        val home =
            _homeCurrency.value


        // Same currency requires no conversion.
        if (currency == home) {

            return 1.0

        }


        val homeRate =
            _rates.value
                .find {
                    it.currencyCode == home
                }
                ?.rateToNZD
                ?: return 0.0


        val targetRate =
            _rates.value
                .find {
                    it.currencyCode == currency
                }
                ?.rateToNZD
                ?: return 0.0


        if (targetRate <= 0.0) {

            return 0.0

        }


        return homeRate / targetRate

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
     * Updates a currency rate using the user's Home Currency
     * as the reference shown in the UI.
     *
     * The database stores:
     *
     * 1 currency = X NZD
     *
     * The UI supplies:
     *
     * 1 Home Currency = X target currency
     *
     * Therefore:
     *
     * target rate to NZD =
     * Home Currency rate to NZD / Home Currency to target rate
     */
    fun updateRate(

        currencyCode: String,

        homeCurrencyRate: Double

    ) {

        viewModelScope.launch {

            if (homeCurrencyRate > 0) {

                val home =
                    _homeCurrency.value


                val homeRateToNZD =
                    _rates.value
                        .find {
                            it.currencyCode == home
                        }
                        ?.rateToNZD
                        ?: return@launch


                if (homeRateToNZD <= 0.0) {

                    return@launch

                }


                val rateToNZD =
                    homeRateToNZD / homeCurrencyRate


                repository.updateRate(

                    currencyCode,

                    rateToNZD

                )

                loadRates()

            }

        }

    }


    /**
     * Diagnostic function for checking the rates and the
     * date actually stored in Room.
     */
    fun checkRates() {

        viewModelScope.launch {

            val rates =
                repository.getAllRates()


            rates.forEach {

                println(
                    "RATE CHECK: " +
                            "${it.currencyCode} = ${it.rateToNZD}, " +
                            "updated = ${it.lastUpdated}"
                )

            }

        }

    }

}