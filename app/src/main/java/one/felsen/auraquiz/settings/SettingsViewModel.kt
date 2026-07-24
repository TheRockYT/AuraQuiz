package one.felsen.auraquiz.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import one.felsen.auraquiz.lock.ProcessManager

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val appTheme = repo.getAppTheme()

    fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch { repo.setAppTheme(appTheme) }
    }

    val isOnLockScreen = repo.isOnLockScreen()

    fun setOnLockScreen(isOnLockScreen: Boolean) {
        if (isOnLockScreen) {
            ProcessManager.checkPermissionAndStartService(repo.context)
        } else {
            ProcessManager.stopLockService(repo.context)
        }
        viewModelScope.launch { repo.setOnLockScreen(isOnLockScreen) }
    }
}
