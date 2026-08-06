package one.felsen.auraquiz.ui.screen

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface Screen : NavKey

@Serializable
object Quiz : Screen

@Serializable
object Settings : Screen

@Serializable
object SettingsDecks : Screen

@Serializable
object SettingsSynchronization : Screen
@Serializable
object SettingsSchedulerScreen : Screen

@Serializable
object DeckCreateScreen : Screen

data class EditDeckScreen(val uuid: Uuid) : Screen

data class DeckOverviewScreen(val uuid: Uuid) : Screen

data class DeckImportScreen(val url: Uri) : Screen

data class CreateCardScreen(val deck: Uuid) : Screen

data class EditCardScreen(val deck: Uuid, val uuid: Uuid) : Screen

sealed interface NavEvent {
    data class NavigateTo(val destination: Screen) : NavEvent
    data object PopBackStack : NavEvent
}
