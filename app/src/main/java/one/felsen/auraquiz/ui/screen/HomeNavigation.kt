package one.felsen.auraquiz.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import one.felsen.auraquiz.data.AppDatabase
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.deck.DeckScreen
import one.felsen.auraquiz.ui.screen.deck.DeckUpsertScreen
import one.felsen.auraquiz.ui.screen.deck.card.CardEditorScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsDecks
import one.felsen.auraquiz.ui.screen.settings.SettingsSchedulerScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsScreen
import one.felsen.auraquiz.ui.screen.settings.SettingsSynchronization


@Composable
fun HomeNavigation(
    settingsViewModel: SettingsViewModel, database: AppDatabase
) {

    val deckRepository = remember { DeckRepository(database.deckDao()) }
    val cardRepository = remember { CardRepository(database.cardDao()) }

    val backStack = rememberNavBackStack(Quiz)

    fun onBack() {
        backStack.removeLastOrNull()
    }

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun replaceScreen(screen: Screen) {
        onBack()
        navigate(screen)
    }

    NavDisplay(
        backStack = backStack, onBack = { onBack() }, entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator()
        ), entryProvider = { key ->
            when (key) {
                is Quiz -> NavEntry(key) {
                    HomeScreen(onOpenSettings = { navigate(Settings) })
                }

                is Settings -> NavEntry(key) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        onBack = { onBack() },
                        onCategorySelect = { navigate(it) })
                }

                is SettingsSynchronization -> NavEntry(key) {
                    SettingsSynchronization(
                        settingsViewModel = settingsViewModel, onBack = { onBack() })
                }

                is SettingsDecks -> NavEntry(key) {
                    SettingsDecks(
                        onBack = { onBack() },
                        onSelectImport = { navigate(DeckImportScreen(it)) },
                        onSelectCreate = { navigate(DeckCreateScreen) },
                        deckRepository = deckRepository,
                        onSelectDeck = {
                            navigate(DeckOverviewScreen(it))
                        })
                }

                is SettingsSchedulerScreen -> NavEntry(key) {
                    SettingsSchedulerScreen(
                        settingsViewModel = settingsViewModel, onBack = { onBack() })
                }

                is DeckCreateScreen -> NavEntry(key) {
                    DeckUpsertScreen(
                        uuid = null,
                        deckRepository = deckRepository,
                        onBack = { onBack() },
                        navigateToDeck = { replaceScreen(DeckOverviewScreen(it)) },
                        onDelete = { onBack() })
                }

                is EditDeckScreen -> NavEntry(key) {
                    DeckUpsertScreen(
                        uuid = key.uuid,
                        deckRepository = deckRepository,
                        onBack = { onBack() },
                        navigateToDeck = { onBack() },
                        onDelete = {
                            onBack()
                            onBack()
                        })
                }

                is DeckImportScreen -> NavEntry(key) {

                }

                is DeckOverviewScreen -> NavEntry(key) {
                    DeckScreen(
                        onBack = { onBack() },
                        deckId = key.uuid,
                        onSelectCard = { cardId ->
                            // Handle card selection
                        }, cardRepository = cardRepository, onEditDeckClick = {
                            navigate(EditDeckScreen(key.uuid))
                        }, onSelectCreateCard = {
                            navigate(CreateCardScreen(key.uuid))
                        })
                }

                is CreateCardScreen -> NavEntry(key) {
                    CardEditorScreen(isEditing = false, onBack = { onBack() }, onSave = { type, map -> }, onDelete = {

                    })
                }

                else -> throw IllegalArgumentException("Unknown screen: $key")
            }
        })
}