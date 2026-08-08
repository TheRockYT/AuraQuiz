package one.felsen.auraquiz.ui.screen.diolog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogComponent(
    canBeDismissed: Boolean = true,
    title: String? = null,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { title?.let { Text(it) } },
        properties = DialogProperties(
            dismissOnBackPress = canBeDismissed,
            dismissOnClickOutside = canBeDismissed,
            usePlatformDefaultWidth = true
        ),
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                content()
            }
        },
        confirmButton = {
            if (canBeDismissed) {
                TextButton(onClick = onDismissRequest) {
                    Text("Dismiss")
                }
            }
        }
    )
}