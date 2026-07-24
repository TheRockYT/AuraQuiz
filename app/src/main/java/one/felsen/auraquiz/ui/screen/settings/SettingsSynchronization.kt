package one.felsen.auraquiz.ui.screen.settings

import androidx.compose.runtime.Composable
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.settings.components.SettingDescriptionRow

@Composable
fun SettingsSynchronization(onBack: () -> Unit, settingsViewModel: SettingsViewModel) {
    SettingsPlane(title = "Synchronization", onBack = onBack) {
        SettingDescriptionRow(
            title = "Synchronization",
            description = "The device-to-device synchronization is not production ready yet. Please check back later."
        )
    }
}
