package com.wandermore.travelcompanion.ui.backup

import android.content.Context
import android.net.Uri

suspend fun readBackupFile(
    context: Context,
    uri: Uri
): String {

    return context.contentResolver
        .openInputStream(uri)
        ?.bufferedReader()
        ?.use { reader ->
            reader.readText()
        }
        ?: throw IllegalStateException(
            "Unable to read the selected backup file."
        )
}