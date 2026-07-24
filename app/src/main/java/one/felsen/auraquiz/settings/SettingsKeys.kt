package one.felsen.auraquiz.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val APP_THEME = stringPreferencesKey("app_theme")
    val IS_ON_LOCK_SCREEN = booleanPreferencesKey("is_on_lockscreen")
}
