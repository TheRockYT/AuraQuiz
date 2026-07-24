package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun <T> SettingSingleChoiceRow(
    title: String,
    options: List<T>,
    selected: T?,
    labelFor: (T) -> String,
    onSelect: (T) -> Unit,
    description: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingCategoryRow(
        title = title,
        description = if (description != null) "$description\n" else "" + if (selected != null) labelFor(
            selected
        ) else "",
        onClick = { showDialog = true }
    )

    if (showDialog) {
        SingleChoiceDialog(
            title = title,
            options = options,
            selected = selected,
            labelFor = labelFor,
            onSelect = onSelect,
            onDismiss = { showDialog = false }
        )
    }
}