package one.felsen.auraquiz.ui.screen.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import one.felsen.auraquiz.settings.AppTheme
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.Screen
import one.felsen.auraquiz.ui.screen.SettingsDecks
import one.felsen.auraquiz.ui.screen.SettingsSchedulerScreen
import one.felsen.auraquiz.ui.screen.SettingsSynchronization
import one.felsen.auraquiz.ui.screen.settings.components.AboutBottomSheet
import one.felsen.auraquiz.ui.screen.settings.components.SettingCategoryRow
import one.felsen.auraquiz.ui.screen.settings.components.SettingCategoryTitle
import one.felsen.auraquiz.ui.screen.settings.components.SettingSingleChoiceRow
import one.felsen.auraquiz.ui.screen.settings.components.SettingToggleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    onCategorySelect: (Screen) -> Unit
) {

    var showAboutSheet by remember { mutableStateOf(false) }

    SettingsPlane(title = "Settings", onBack = onBack) {
        SettingCategoryTitle("Content")
        SettingCategoryRow(
            title = "Decks",
            description = "Setup the decks",
            onClick = { onCategorySelect(SettingsDecks) }
        )
        SettingCategoryRow(
            title = "Synchronization",
            description = "Unavailable",
            onClick = { onCategorySelect(SettingsSynchronization) }
        )

        SettingCategoryRow(
            title = "Scheduler",
            description = "Setup how the scheduler works",
            onClick = { onCategorySelect(SettingsSchedulerScreen) }
        )


        HorizontalDivider()

        SettingCategoryTitle("General")

        val appTheme by settingsViewModel.appTheme.collectAsStateWithLifecycle(null)

        SettingSingleChoiceRow(
            title = "Appearance",
            options = AppTheme.entries,
            selected = appTheme,
            labelFor = { it.label },
            onSelect = { settingsViewModel.setAppTheme(it) }
        )

        val isOnLockscreen by settingsViewModel.isOnLockScreen.collectAsStateWithLifecycle(false)
        val context = LocalContext.current

        SettingToggleRow(
            title = "Show on lockscreen",
            description = "Shows the quiz on the lockscreen",
            checked = isOnLockscreen && Settings.canDrawOverlays(context),
            onCheckedChange = {
                if (!Settings.canDrawOverlays(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${context.packageName}".toUri(),
                    )
                    context.startActivity(intent)
                    return@SettingToggleRow
                }

                settingsViewModel.setOnLockScreen(!isOnLockscreen)
            }
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