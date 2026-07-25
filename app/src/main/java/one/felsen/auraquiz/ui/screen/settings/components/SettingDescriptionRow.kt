package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingDescriptionRow(title: String, description: String) {
    ListItem(
        modifier = Modifier,
        leadingContent = null,
        trailingContent = null,
        overlineContent = null,
        supportingContent = { Text(description) },
        colors = ListItemDefaults.colors(),
        elevation = ListItemDefaults.elevation(ListItemDefaults.Elevation),
        content = { Text(title) },
    )
}