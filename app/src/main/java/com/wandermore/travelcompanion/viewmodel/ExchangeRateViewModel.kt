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
     * This is used for display and editing in the
     * Currency Exchange Rates screen.
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
     * Example:
     *
     * Home Currency = NZD
     * AUD rate = 1.1970 NZD
     *
     * 1 NZD = 1.0 / 1.1970
     *        = 0.8354 AUD
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