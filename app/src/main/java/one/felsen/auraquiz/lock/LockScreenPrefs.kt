package one.felsen.auraquiz.lock

import android.content.Context
import androidx.core.content.edit

object LockScreenPrefs {
    private const val PREFS_NAME = "lock_screen_prefs"
    private const val KEY_ENABLED = "is_enabled"

    fun setServiceEnabled(context: Context, isEnabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_ENABLED, isEnabled) }
    }

    fun isServiceEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ENABLED, false) // Default to false
    }
}
