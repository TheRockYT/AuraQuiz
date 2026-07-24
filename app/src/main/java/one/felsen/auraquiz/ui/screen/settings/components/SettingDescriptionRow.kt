package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SettingDescriptionRow(title: String, description: String) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) }
    )
}