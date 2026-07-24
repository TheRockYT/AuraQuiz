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
) {
    var showSheet by remember { mutableStateOf(false) }

    SettingCategoryRow(
        title = title,
        description = if(selected!= null) labelFor(selected) else "",
        onClick = { showSheet = true }
    )
    if (showSheet) {
        SingleChoiceBottomSheet(
            title = title,
            options = options,
            selected = selected,
            labelFor = { labelFor(it) },
            onSelect = onSelect,
            onDismiss = { showSheet = false }
        )
    }
}