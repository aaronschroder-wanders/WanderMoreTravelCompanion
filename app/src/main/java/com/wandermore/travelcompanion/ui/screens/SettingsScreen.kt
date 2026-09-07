package com.wandermore.travelcompanion.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    //
    // This moves any existing HTTP/HTTPS passport links from the
    // old temporary preference file into the production file.
    //
    // Old content:// document URIs are deliberately ignored
    // because they are device-specific and cannot be restored
    // to another phone.
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
    //
    // settingsRefreshKey is deliberately used as the remember
    // key. After a successful restore, AppNavigation increments
    // this value, causing these states to be recreated from the
    // restored SharedPreferences values.
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
            Arrangement.spacedBy(12.dp)
    ) {

        // =====================================================
        // TITLE
        // =====================================================

        Text(
            text = "Settings",
            style =
                MaterialTheme.typography.headlineMedium
        )

        // =====================================================
        // CURRENCY
        // =====================================================

        Text(
            text = "CURRENCY",
            style =
                MaterialTheme.typography.labelLarge,

            modifier = Modifier.padding(
                top = 4.dp
            )
        )

        // =====================================================
        // HOME CURRENCY
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(12.dp),

                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Home Currency",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                        "Used as the currency for new trips and calculations."
                )

                CurrencyDropdown(
                    selectedCurrency =
                        homeCurrency,

                    onCurrencySelected = { currency ->

                        userSettingsViewModel
                            .setHomeCurrency(currency)
                    },

                    label = "Home Currency"
                )
            }
        }

        // =====================================================
        // EXCHANGE RATES
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Button(
                onClick =
                    onExchangeRates,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Currency Exchange Rates"
                )
            }
        }

        // =====================================================
        // DESTINATIONS
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Button(
                onClick =
                    onDestinations,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Destinations"
                )
            }
        }

        // =====================================================
        // DATA & BACKUP
        // =====================================================

        Text(
            text = "DATA & BACKUP",
            style =
                MaterialTheme.typography.labelLarge,

            modifier = Modifier.padding(
                top = 4.dp
            )
        )

        // =====================================================
        // BACKUP
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Button(
                onClick =
                    onBackup,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Backup to Google Drive"
                )
            }
        }

        // =====================================================
        // RESTORE
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Button(
                onClick =
                    onRestore,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Restore from Google Drive"
                )
            }
        }

        // =====================================================
        // PASSPORT DOCUMENTS
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(12.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text =
                        "Passport Documents",

                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                        "Add web links to the passports for both app users."
                )

                // =================================================
                // PRIMARY USER PASSPORT
                // =================================================

                Text(
                    text =
                        "Primary User Passport",

                    style =
                        MaterialTheme.typography.titleSmall,

                    modifier = Modifier.padding(
                        top = 2.dp
                    )
                )

                if (editingPrimaryPassport) {

                    OutlinedTextField(
                        value =
                            primaryPassportLink,

                        onValueChange = {
                            primaryPassportLink = it
                            primaryPassportError = ""
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Web Link")
                        },

                        placeholder = {
                            Text("Paste web link")
                        },

                        singleLine = true,

                        isError =
                            primaryPassportError.isNotBlank()
                    )

                    if (
                        primaryPassportError.isNotBlank()
                    ) {

                        Text(
                            text =
                                primaryPassportError,

                            style =
                                MaterialTheme.typography.bodySmall,

                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {

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

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            primaryPassportLink
                                .trim()
                                .isNotEmpty()
                    ) {

                        Text(
                            text =
                                "Link Passport"
                        )
                    }

                } else {

                    Text(
                        text =
                            "✓ Passport linked",

                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            Color(0xFF008000)
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = {

                                openWebLink(
                                    context,
                                    savedPrimaryPassportLink
                                )
                            },

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Open Passport"
                            )
                        }

                        Button(
                            onClick = {

                                primaryPassportLink =
                                    ""

                                primaryPassportError =
                                    ""

                                editingPrimaryPassport =
                                    true
                            },

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Change Link"
                            )
                        }
                    }
                }

                // =================================================
                // TRAVEL PARTNER PASSPORT
                // =================================================

                Text(
                    text =
                        "Travel Partner Passport",

                    style =
                        MaterialTheme.typography.titleSmall,

                    modifier = Modifier.padding(
                        top = 6.dp
                    )
                )

                if (editingTravelPartnerPassport) {

                    OutlinedTextField(
                        value =
                            travelPartnerPassportLink,

                        onValueChange = {
                            travelPartnerPassportLink = it
                            travelPartnerPassportError = ""
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Web Link")
                        },

                        placeholder = {
                            Text("Paste web link")
                        },

                        singleLine = true,

                        isError =
                            travelPartnerPassportError
                                .isNotBlank()
                    )

                    if (
                        travelPartnerPassportError
                            .isNotBlank()
                    ) {

                        Text(
                            text =
                                travelPartnerPassportError,

                            style =
                                MaterialTheme.typography.bodySmall,

                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {

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

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            travelPartnerPassportLink
                                .trim()
                                .isNotEmpty()
                    ) {

                        Text(
                            text =
                                "Link Passport"
                        )
                    }

                } else {

                    Text(
                        text =
                            "✓ Passport linked",

                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            Color(0xFF008000)
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = {

                                openWebLink(
                                    context,
                                    savedTravelPartnerPassportLink
                                )
                            },

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Open Passport"
                            )
                        }

                        Button(
                            onClick = {

                                travelPartnerPassportLink =
                                    ""

                                travelPartnerPassportError =
                                    ""

                                editingTravelPartnerPassport =
                                    true
                            },

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Change Link"
                            )
                        }
                    }
                }
            }
        }

        // =====================================================
        // WANDER MORE WORK LESS
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(12.dp),

                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text =
                        "Wander More Work Less",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF00A6A6)
                )

                Text(
                    text =
                        "Travel videos, tips and adventures.",

                    style =
                        MaterialTheme.typography.bodyLarge
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

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            "Visit our YouTube Channel"
                    )
                }
            }
        }

        // =====================================================
        // BACK
        // =====================================================

        Button(
            onClick =
                onBack
        ) {

            Text(
                text =
                    "Back"
            )
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