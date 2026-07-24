package one.felsen.auraquiz.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.settings.SettingsDecks
import one.felsen.auraquiz.ui.screen.settings.SettingsScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsSynchronization


@Composable
fun HomeNavigation(settingsViewModel: SettingsViewModel) {
    val backStack = rememberNavBackStack(Quiz)

    fun onBack() {
        backStack.removeLastOrNull()
    }

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Quiz> {
                HomeScreen(onOpenSettings = { navigate(Settings) })
            }
            entry<Settings> {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onBack = { onBack() },
                    onCategorySelect = { navigate(it) }
                )
            }
            entry<SettingsSynchronization> {
                SettingsSynchronization(
                    settingsViewModel = settingsViewModel,
                    onBack = { onBack() }
                )
            }
            entry<SettingsDecks> {
                SettingsDecks(
                    settingsViewModel = settingsViewModel,
                    onBack = { onBack() }
                )
            }
        }
    )
}