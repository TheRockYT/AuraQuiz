package one.felsen.auraquiz.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "settings")


class SettingsRepository(val context: Context) {


    fun getAppTheme(): Flow<AppTheme> {
        return getEnumFlow(SettingsKeys.APP_THEME, AppTheme.SYSTEM)
    }

    suspend fun setAppTheme(theme: AppTheme) {
        setEnum(SettingsKeys.APP_THEME, theme)
    }

    fun isOnLockScreen(): Flow<Boolean> {
        return getFlow(SettingsKeys.IS_ON_LOCK_SCREEN, false)
    }

    suspend fun setOnLockScreen(isOnLockScreen: Boolean) {
        set(SettingsKeys.IS_ON_LOCK_SCREEN, isOnLockScreen)
    }


    // Storage part
    inline fun <reified T : Enum<T>> getEnumFlow(k: Preferences.Key<String>, defaultValue: T): Flow<T> {
        return getFlow(k, defaultValue.name).map { str ->  enumValueOf<T>(str) }
    }

    suspend inline fun <reified T : Enum<T>> setEnum(k: Preferences.Key<String>, value: T) {
        set(k, value.name)
    }

    fun <T> getFlow(k: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data
            .map { prefs -> prefs[k] ?: defaultValue }
    }

    suspend fun <T> set(k: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs -> prefs[k] = value }
    }
}