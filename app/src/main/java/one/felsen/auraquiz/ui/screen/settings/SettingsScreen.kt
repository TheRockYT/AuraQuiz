package one.felsen.auraquiz.ui.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import one.felsen.auraquiz.ui.screen.Screen
import one.felsen.auraquiz.ui.screen.SettingsDecks
import one.felsen.auraquiz.ui.screen.SettingsSynchronization
import one.felsen.auraquiz.ui.screen.settings.components.AboutBottomSheet
import one.felsen.auraquiz.ui.screen.settings.components.SettingCategoryRow
import one.felsen.auraquiz.ui.screen.settings.components.SettingCategoryTitle
import one.felsen.auraquiz.ui.screen.settings.components.SettingToggleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onCategorySelect: (Screen) -> Unit) {

    var showAboutSheet by remember { mutableStateOf(false) }

    SettingsPlane(title = "Settings", onBack = onBack) {
        SettingCategoryTitle("Content")
        SettingCategoryRow(
            title = "Decks",
            description = "Active decks: -",
            onClick = { onCategorySelect(SettingsDecks) }
        )
        SettingCategoryRow(
            title = "Synchronization",
            description = "Unavailable",
            onClick = { onCategorySelect(SettingsSynchronization) }
        )
        HorizontalDivider()

        SettingCategoryTitle("General")

        SettingToggleRow(
            title = "Show on lockscreen",
            description = "The device may be locked and accessed by other persons.",
            checked = true,
            onCheckedChange = { }
        )

        SettingCategoryRow(
            title = "About",
            onClick = { showAboutSheet = true }
        )

        if (showAboutSheet) {
            AboutBottomSheet(onDismiss = { showAboutSheet = false })
        }
    }
}