package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SettingNumberEntryRow(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    description: String
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingCategoryRow(
        title = title,
        description = description + "\n$value",
        onClick = { showDialog = true }
    )

    if (showDialog) {
        var textState by remember(value) { mutableStateOf(value.toString()) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                OutlinedTextField(
                    value = textState,
                    onValueChange = { input ->
                        // Only allow numeric input
                        if (input.all { it.isDigit() }) {
                            textState = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        textState.toIntOrNull()?.let(onValueChange)
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