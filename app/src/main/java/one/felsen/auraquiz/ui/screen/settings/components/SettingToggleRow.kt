package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.let

@Composable
fun SettingToggleRow(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    ListItem(
        modifier = Modifier.clickable { onCheckedChange(!checked) },
        leadingContent = null,
        trailingContent = {
                Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
            },
        overlineContent = null,
        supportingContent = description?.let { { Text(it) } },
        colors = ListItemDefaults.colors(),
        elevation = ListItemDefaults.elevation(ListItemDefaults.Elevation),
        content = { Text(title) },
    )
}
