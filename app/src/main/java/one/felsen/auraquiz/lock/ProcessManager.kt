package one.felsen.auraquiz.lock

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri

object ProcessManager {
    fun stopLockService(context: Context) {
        Log.d("Overlay", "Stopping service...")
        val serviceIntent = Intent(context, LockScreenService::class.java)
        context.stopService(serviceIntent)
    }

    fun checkPermissionAndStartService(context: Context) {
        Log.d("Overlay", "Attempting to start service...")
        if (!Settings.canDrawOverlays(context)) {
            Log.d("Overlay", "No permission to draw overlays")

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            context.startActivity(intent)
        } else {
            Log.d("Overlay", "Permission granted")

            val serviceIntent = Intent(context, LockScreenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("Overlay", "Starting foreground service")
                context.startForegroundService(serviceIntent)
            } else {
                Log.d("Overlay", "Starting service")
                context.startService(serviceIntent)
            }
        }
    }

}