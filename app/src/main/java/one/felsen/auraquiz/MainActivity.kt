@file:OptIn(ExperimentalMaterial3Api::class)

package one.felsen.auraquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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

            val settingsViewModel =
                viewModel { SettingsViewModel(SettingsRepository(context)) }

            val theme by settingsViewModel.appTheme.collectAsState(AppTheme.SYSTEM)

            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            AuraQuizTheme(darkTheme = darkTheme) {
                HomeNavigation(settingsViewModel = settingsViewModel)
            }
        }
    }
}
