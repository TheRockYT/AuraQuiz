package one.felsen.auraquiz.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import one.felsen.auraquiz.data.AppDatabase
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.deck.DeckEditorScreen
import one.felsen.auraquiz.ui.screen.deck.DeckViewModel
import one.felsen.auraquiz.ui.screen.settings.SettingsDecks
import one.felsen.auraquiz.ui.screen.settings.SettingsSchedulerScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsSynchronization


@Composable
fun HomeNavigation(
    settingsViewModel: SettingsViewModel,
    database: AppDatabase
) {

    val deckViewModel = viewModel { DeckViewModel(DeckRepository(database.deckDao())) }

    val backStack = rememberNavBackStack(Quiz)

    fun onBack() {
        backStack.removeLastOrNull()
    }

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    LaunchedEffect(Unit) {
        deckViewModel.navEvents.collect { event ->
            when (event) {
                is NavEvent.NavigateTo -> navigate(event.destination)
                NavEvent.PopBackStack -> onBack()
            }
        }
    }
    val isDeckCreating by deckViewModel.creatingDeck.collectAsStateWithLifecycle()

    LoadingDialog(isLoading = isDeckCreating)

    NavDisplay(
        backStack = backStack,
        onBack = { onBack() },
        entryProvider = { key ->
            when (key) {
                is Quiz -> NavEntry(key) {
                    HomeScreen(onOpenSettings = { navigate(Settings) })
                }

                is Settings -> NavEntry(key) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        onBack = { onBack() },
                        onCategorySelect = { navigate(it) }
                    )
                }

                is SettingsSynchronization -> NavEntry(key) {
                    SettingsSynchronization(
                        settingsViewModel = settingsViewModel,
                        onBack = { onBack() }
                    )
                }

                is SettingsDecks -> NavEntry(key) {
                    SettingsDecks(
                        onBack = { onBack() },
                        onSelectImport = { navigate(DeckImportScreen(it)) },
                        onSelectCreate = { navigate(DeckCreateScreen) },
                        deckViewModel = deckViewModel,
                        onSelectDeck = {
                            navigate(DeckOverviewScreen(it))
                        }
                    )
                }

                is SettingsSchedulerScreen -> NavEntry(key) {
                    SettingsSchedulerScreen(
                        settingsViewModel = settingsViewModel,
                        onBack = { onBack() }
                    )
                }

                is DeckCreateScreen -> NavEntry(key) {
                    DeckEditorScreen(
                        isEditMode = false,
                        onSave = { name, description, authors, _ ->
                            onBack()
                            deckViewModel.createDeck(name, description, authors)
                        },
                        onCancel = { onBack() }
                    )
                }

                is DeckImportScreen -> NavEntry(key) {

                }

                is DeckOverviewScreen -> NavEntry(key) {
                    Text(key.uuid.toString())
                }

                else -> throw IllegalArgumentException("Unknown screen: $key")
            }
        }
    )
}