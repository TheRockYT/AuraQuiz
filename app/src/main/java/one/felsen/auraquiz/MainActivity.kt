package one.felsen.auraquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.AppDatabase
import one.felsen.auraquiz.settings.AppTheme
import one.felsen.auraquiz.settings.SettingsRepository
import one.felsen.auraquiz.settings.SettingsViewModel
import one.felsen.auraquiz.ui.screen.HomeNavigation
import one.felsen.auraquiz.ui.theme.AuraQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current.applicationContext

            val database = AppDatabase.getInstance(context)

            val settingsRepository = remember { SettingsRepository(context) }

            val settingsViewModel =
                viewModel { SettingsViewModel(settingsRepository) }

            val theme by settingsViewModel.appTheme.collectAsState(AppTheme.SYSTEM)
            val dynamicColor by settingsViewModel.dynamicColor.collectAsState(false)

            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            AuraQuizTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                HomeNavigation(settingsViewModel = settingsViewModel, database = database, settingsRepository = settingsRepository)
            }
        }
    }
}
