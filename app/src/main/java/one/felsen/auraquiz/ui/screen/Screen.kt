package one.felsen.auraquiz.ui.screen

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

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

object SettingsSchedulerScreen : Screen
