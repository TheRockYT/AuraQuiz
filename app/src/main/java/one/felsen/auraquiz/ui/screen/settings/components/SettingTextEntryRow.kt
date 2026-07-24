package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun SettingTextEntryRow(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    description: String
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingCategoryRow(
        title = title,
        description = description + "\n$value",
        onClick = { showDialog = true }
    )

    if (showDialog) {
        var textState by remember(value) { mutableStateOf(value) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                OutlinedTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(textState)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}