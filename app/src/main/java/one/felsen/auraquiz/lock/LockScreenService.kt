package one.felsen.auraquiz.lock

import android.R.drawable
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver

class LockScreenService : Service() {

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("LockScreenService", "Received screen on event: ${intent.action}")
            if (intent.action != Intent.ACTION_SCREEN_ON) {
                Log.d("LockScreenService", "Received unexpected intent: ${intent.action}")
                return
            }

            val keyguardManager =
                context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (!keyguardManager.isKeyguardLocked) {
                Log.d("LockScreenService", "Keyguard dismissed. Not launching lock UI.")
                return
            }

            if (LockScreenActivity.isShowing) {
                Log.d("LockScreenService", "LockScreenActivity is already showing!")
                return
            }

            Log.d("LockScreenService", "Attempting to launch lock UI for ${intent.action}")

            val lockIntent = Intent(context, LockScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(lockIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("LockScreenService", "Created LockScreenService")

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "lock_service_channel")
            .setContentTitle("Lock Screen Active")
            .setContentText("Listening for screen wake...")
            .setSmallIcon(drawable.ic_lock_lock)
            .build()

        startForeground(1, notification)

        // Register receiver for screen on
        registerReceiver(
            this,
            screenReceiver,
            IntentFilter(Intent.ACTION_SCREEN_ON),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("LockScreenService", "Destroyed LockScreenService")
        unregisterReceiver(screenReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Channel for the foreground service (low importance)
            val serviceChannel = NotificationChannel(
                "lock_service_channel",
                "Lock Screen Service",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
