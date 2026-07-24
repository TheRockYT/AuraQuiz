package one.felsen.auraquiz.lock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import one.felsen.auraquiz.settings.SettingsRepository

class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {

        // Double-check the action to prevent spoofed intents
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.d("BootReceiver", "Boot completed event received!");


            val pendingResult = goAsync()

            scope.launch {
                val settingsRepository = SettingsRepository(context)
                val isOnLockScreen = settingsRepository.isOnLockScreen().first()

                // Check if the user left the service enabled AND if we still have permission
                if (isOnLockScreen && Settings.canDrawOverlays(context)) {
                    ProcessManager.checkPermissionAndStartService(context)
                }
                pendingResult.finish()
            }
        }
    }
}
