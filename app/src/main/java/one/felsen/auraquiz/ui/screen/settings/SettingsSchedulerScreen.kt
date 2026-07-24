package one.felsen.auraquiz.ui.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import one.felsen.auraquiz.settings.FastSkip
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.settings.components.SettingCategoryTitle
import one.felsen.auraquiz.ui.screen.settings.components.SettingNumberEntryRow
import one.felsen.auraquiz.ui.screen.settings.components.SettingSingleChoiceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSchedulerScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {

    SettingsPlane(title = "Scheduler", onBack = onBack) {
        SettingCategoryTitle("Setup")

        val fastSkip by settingsViewModel.fastSkip.collectAsStateWithLifecycle(null)

        SettingSingleChoiceRow(
            title = "Fast-skip",
            options = FastSkip.entries,
            selected = fastSkip,
            labelFor = { it.label },
            onSelect = { settingsViewModel.setFastSkip(it) },
            description = "Skips automatically once a task is completed",
        )

        val maxNew by settingsViewModel.maxNew.collectAsStateWithLifecycle(0)

        SettingNumberEntryRow(
            title = "Max new per day",
            value = maxNew,
            onValueChange = { settingsViewModel.setMaxNew(it) },
            description = "The maximum of new cards per day",
        )
    }
}