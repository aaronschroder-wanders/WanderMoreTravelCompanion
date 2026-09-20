package com.wandermore.travelcompanion.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel
import java.net.URI
import java.net.URISyntaxException

@Composable
fun SettingsScreen(
    settingsRefreshKey: Int,
    userSettingsViewModel: UserSettingsViewModel,
    onExchangeRates: () -> Unit,
    onDestinations: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onExportTripExpenses: () -> Unit,
    onExportItinerary: () -> Unit,
    onBack: () -> Unit
) {
    val homeCurrency by userSettingsViewModel
        .homeCurrency
        .collectAsState()

    val context = LocalContext.current

    // =========================================================
    // USER LINK PREFERENCES
    // =========================================================

    val preferences = remember {
        context.getSharedPreferences(
            "user_link_preferences",
            Context.MODE_PRIVATE
        )
    }

    // =========================================================
    // MIGRATE EXISTING PASSPORT LINKS
    // =========================================================

    remember {

        val oldPreferences =
            context.getSharedPreferences(
                "document_test_preferences",
                Context.MODE_PRIVATE
            )

        val existingPrimaryLink =
            preferences.getString(
                "primary_user_passport_link",
                ""
            ) ?: ""

        val existingPartnerLink =
            preferences.getString(
                "travel_partner_passport_link",
                ""
            ) ?: ""

        val oldPrimaryLink =
            oldPreferences.getString(
                "primary_user_passport_link",
                ""
            ) ?: ""

        val oldPartnerLink =
            oldPreferences.getString(
                "travel_partner_passport_link",
                ""
            ) ?: ""

        val oldPassportUri =
            oldPreferences.getString(
                "passport_aaron_uri",
                ""
            ) ?: ""

        val editor =
            preferences.edit()

        // -----------------------------------------------------
        // PRIMARY USER
        // -----------------------------------------------------

        if (existingPrimaryLink.isBlank()) {

            when {

                isValidWebLink(oldPrimaryLink) -> {

                    editor.putString(
                        "primary_user_passport_link",
                        oldPrimaryLink
                    )
                }

                isValidWebLink(oldPassportUri) -> {

                    editor.putString(
                        "primary_user_passport_link",
                        oldPassportUri
                    )
                }
            }
        }

        // -----------------------------------------------------
        // TRAVEL PARTNER
        // -----------------------------------------------------

        if (
            existingPartnerLink.isBlank() &&
            isValidWebLink(oldPartnerLink)
        ) {

            editor.putString(
                "travel_partner_passport_link",
                oldPartnerLink
            )
        }

        editor.apply()
    }

    // =========================================================
    // PRIMARY USER PASSPORT
    // =========================================================

    var primaryPassportLink by remember(settingsRefreshKey) {
        mutableStateOf(
            preferences.getString(
                "primary_user_passport_link",
                ""
            ) ?: ""
        )
    }

    var savedPrimaryPassportLink by remember(settingsRefreshKey) {
        mutableStateOf(
            preferences.getString(
                "primary_user_passport_link",
                ""
            ) ?: ""
        )
    }

    var editingPrimaryPassport by remember(settingsRefreshKey) {
        mutableStateOf(
            savedPrimaryPassportLink.isBlank()
        )
    }

    var primaryPassportError by remember(settingsRefreshKey) {
        mutableStateOf("")
    }

    // =========================================================
    // TRAVEL PARTNER PASSPORT
    // =========================================================

    var travelPartnerPassportLink by remember(settingsRefreshKey) {
        mutableStateOf(
            preferences.getString(
                "travel_partner_passport_link",
                ""
            ) ?: ""
        )
    }

    var savedTravelPartnerPassportLink by remember(settingsRefreshKey) {
        mutableStateOf(
            preferences.getString(
                "travel_partner_passport_link",
                ""
            ) ?: ""
        )
    }

    var editingTravelPartnerPassport by remember(settingsRefreshKey) {
        mutableStateOf(
            savedTravelPartnerPassportLink.isBlank()
        )
    }

    var travelPartnerPassportError by remember(settingsRefreshKey) {
        mutableStateOf("")
    }

    // =========================================================
    // SETTINGS CONTENT
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        // =====================================================
        // TITLE
        // =====================================================

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                bottom = 4.dp
            )
        )

        // =====================================================
        // CURRENCY
        // =====================================================

        SettingsSectionHeading(
            text = "CURRENCY"
        )

        // -----------------------------------------------------
        // HOME CURRENCY
        // -----------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Home Currency",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                        "Used as the currency for new trips and calculations.",
                    style = MaterialTheme.typography.bodyMedium
                )

                CurrencyDropdown(
                    selectedCurrency = homeCurrency,

                    onCurrencySelected = { currency ->

                        userSettingsViewModel
                            .setHomeCurrency(currency)
                    },

                    label = "Home Currency"
                )
            }
        }

        // -----------------------------------------------------
        // EXCHANGE RATES
        // -----------------------------------------------------

        SettingsActionCard(
            text = "Currency Exchange Rates",
            onClick = onExchangeRates
        )

        // =====================================================
        // DESTINATIONS
        // =====================================================

        SettingsSectionHeading(
            text = "DESTINATIONS"
        )

        SettingsActionCard(
            text = "Manage Destinations",
            onClick = onDestinations
        )

        // =====================================================
        // PASSPORT DOCUMENTS
        // =====================================================

        SettingsSectionHeading(
            text = "PASSPORT DOCUMENTS"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text =
                        "Add web links to the passports for both app users.",
                    style = MaterialTheme.typography.bodyMedium
                )

                // =================================================
                // PRIMARY USER
                // =================================================

                PassportSettingRow(
                    name = "Primary User",

                    link = primaryPassportLink,

                    editing = editingPrimaryPassport,

                    error = primaryPassportError,

                    onLinkChanged = {
                        primaryPassportLink = it
                        primaryPassportError = ""
                    },

                    onSave = {

                        val cleanedLink =
                            primaryPassportLink.trim()

                        if (
                            !isValidWebLink(
                                cleanedLink
                            )
                        ) {

                            primaryPassportError =
                                "Please enter a valid web link starting with https://"

                        } else {

                            preferences.edit()
                                .putString(
                                    "primary_user_passport_link",
                                    cleanedLink
                                )
                                .apply()

                            savedPrimaryPassportLink =
                                cleanedLink

                            primaryPassportLink =
                                cleanedLink

                            primaryPassportError =
                                ""

                            editingPrimaryPassport =
                                false
                        }
                    },

                    onOpen = {

                        openWebLink(
                            context,
                            savedPrimaryPassportLink
                        )
                    },

                    onChange = {

                        primaryPassportLink = ""

                        primaryPassportError = ""

                        editingPrimaryPassport = true
                    }
                )

                // =================================================
                // TRAVEL PARTNER
                // =================================================

                PassportSettingRow(
                    name = "Travel Partner",

                    link = travelPartnerPassportLink,

                    editing =
                        editingTravelPartnerPassport,

                    error =
                        travelPartnerPassportError,

                    onLinkChanged = {
                        travelPartnerPassportLink = it
                        travelPartnerPassportError = ""
                    },

                    onSave = {

                        val cleanedLink =
                            travelPartnerPassportLink.trim()

                        if (
                            !isValidWebLink(
                                cleanedLink
                            )
                        ) {

                            travelPartnerPassportError =
                                "Please enter a valid web link starting with https://"

                        } else {

                            preferences.edit()
                                .putString(
                                    "travel_partner_passport_link",
                                    cleanedLink
                                )
                                .apply()

                            savedTravelPartnerPassportLink =
                                cleanedLink

                            travelPartnerPassportLink =
                                cleanedLink

                            travelPartnerPassportError =
                                ""

                            editingTravelPartnerPassport =
                                false
                        }
                    },

                    onOpen = {

                        openWebLink(
                            context,
                            savedTravelPartnerPassportLink
                        )
                    },

                    onChange = {

                        travelPartnerPassportLink = ""

                        travelPartnerPassportError = ""

                        editingTravelPartnerPassport = true
                    }
                )
            }
        }

        // =====================================================
        // DATA & BACKUP
        // =====================================================

        SettingsSectionHeading(
            text = "DATA & BACKUP"
        )

        SettingsActionCard(
            text = "Backup to Google Drive",
            onClick = onBackup
        )

        SettingsActionCard(
            text = "Restore from Google Drive",
            onClick = onRestore
        )

        SettingsActionCard(
            text = "Export Trip Expenses (CSV)",
            onClick = onExportTripExpenses
        )

        SettingsActionCard(
            text = "Export Itinerary (CSV)",
            onClick = onExportItinerary
        )

        // =====================================================
        // WANDER MORE WORK LESS
        // =====================================================

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Wander More Work Less",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A6A6)
                )

                Text(
                    text = "Travel videos, tips and adventures.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = {

                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://www.youtube.com/@WanderMoreWorkLess"
                                )
                            )

                        context.startActivity(intent)
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Visit our YouTube Channel"
                    )
                }
            }
        }

        // =====================================================
        // BACK
        // =====================================================

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Back"
            )
        }

        Spacer(
            modifier = Modifier.padding(
                bottom = 4.dp
            )
        )
    }
}

// =============================================================
// SETTINGS SECTION HEADING
// =============================================================

@Composable
private fun SettingsSectionHeading(
    text: String
) {

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            top = 6.dp,
            bottom = 1.dp
        )
    )
}

// =============================================================
// SIMPLE SETTINGS ACTION CARD
// =============================================================

@Composable
private fun SettingsActionCard(
    text: String,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 14.dp
                )
        )
    }
}

// =============================================================
// PASSPORT SETTING ROW
// =============================================================

@Composable
private fun PassportSettingRow(
    name: String,
    link: String,
    editing: Boolean,
    error: String,
    onLinkChanged: (String) -> Unit,
    onSave: () -> Unit,
    onOpen: () -> Unit,
    onChange: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        if (editing) {

            OutlinedTextField(
                value = link,

                onValueChange = onLinkChanged,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Web Link")
                },

                placeholder = {
                    Text("Paste web link")
                },

                singleLine = true,

                isError =
                    error.isNotBlank()
            )

            if (error.isNotBlank()) {

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = link.trim().isNotEmpty()
            ) {

                Text(
                    text = "Save Link"
                )
            }

        } else {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "✓ Linked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            vertical = 10.dp
                        )
                )

                Button(
                    onClick = onOpen
                ) {

                    Text(
                        text = "Open"
                    )
                }

                Button(
                    onClick = onChange
                ) {

                    Text(
                        text = "Change"
                    )
                }
            }
        }
    }
}

// =============================================================
// WEB LINK VALIDATION
// =============================================================

private fun isValidWebLink(
    link: String
): Boolean {

    if (link.isBlank()) {
        return false
    }

    return try {

        val uri =
            URI(link)

        val scheme =
            uri.scheme?.lowercase()

        !uri.host.isNullOrBlank() &&
                (
                        scheme == "http" ||
                                scheme == "https"
                        )

    } catch (
        exception: URISyntaxException
    ) {

        false
    }
}

// =============================================================
// SAFE WEB LINK OPENING
// =============================================================

private fun openWebLink(
    context: Context,
    link: String
) {

    if (!isValidWebLink(link)) {
        return
    }

    try {

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )

        context.startActivity(intent)

    } catch (
        exception: Exception
    ) {

        // Deliberately do nothing here.
        // The app must never crash because a saved web link
        // cannot be opened.
    }
}