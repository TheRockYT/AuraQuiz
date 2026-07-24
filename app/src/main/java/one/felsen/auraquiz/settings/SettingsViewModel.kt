package one.felsen.auraquiz.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val appTheme = repo.getAppTheme()

    fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch { repo.setAppTheme(appTheme) }
    }
}
