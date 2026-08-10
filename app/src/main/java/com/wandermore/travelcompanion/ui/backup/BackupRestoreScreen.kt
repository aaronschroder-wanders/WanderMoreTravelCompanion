package com.wandermore.travelcompanion.ui.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.data.repository.RestoreRepository
import com.wandermore.travelcompanion.database.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun BackupRestoreScreen(
    database: AppDatabase
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var message by remember {
        mutableStateOf("")
    }

    var isRestoring by remember {
        mutableStateOf(false)
    }

    val restoreRepository =
        remember(database) {
            RestoreRepository(database)
        }

    val filePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            scope.launch {

                isRestoring = true
                message = ""

                try {

                    val json =
                        readBackupFile(
                            context = context,
                            uri = uri
                        )

                    restoreRepository.restoreBackup(json)

                    message = "Backup restored successfully."

                } catch (e: Exception) {

                    message =
                        "Restore failed: ${e.message}"

                } finally {

                    isRestoring = false
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Backup & Restore"
        )

        Button(
            onClick = {
                filePicker.launch(
                    arrayOf(
                        "application/json",
                        "text/plain",
                        "*/*"
                    )
                )
            },
            enabled = !isRestoring,
            modifier = Modifier.padding(top = 24.dp)
        ) {

            Text(
                text =
                    if (isRestoring)
                        "Restoring..."
                    else
                        "Restore Backup"
            )
        }

        if (message.isNotEmpty()) {

            Text(
                text = message,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}