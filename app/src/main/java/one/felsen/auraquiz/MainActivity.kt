package one.felsen.auraquiz

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import one.felsen.auraquiz.ui.theme.AuraQuizTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraQuizTheme {


                val isRunning by LockScreenService.isServiceRunning.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {

                        Text(text = if (isRunning) "Status: Active" else "Status: Inactive")

                        // 3. Toggle button
                        Button(onClick = {
                            if (isRunning) {
                                stopLockService()
                            } else {
                                checkPermissionAndStartService()
                            }
                        }) {
                            Text(if (isRunning) "Stop Lock Screen" else "Enable Lock Screen")
                        }
                    }
                }


            }
        }
    }

    private fun stopLockService() {
        Log.d("AQOverlay", "Stopping service...")
        LockScreenPrefs.setServiceEnabled(this, false)
        val serviceIntent = Intent(this, LockScreenService::class.java)
        stopService(serviceIntent)
    }

    private fun checkPermissionAndStartService() {
        Log.d("AQOverlay", "Attempting to start service...")
        if (!Settings.canDrawOverlays(this)) {
            Log.d("AQOverlay", "No permission to draw overlays")

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivity(intent)
        } else {
            Log.d("AQOverlay", "Permission granted")

            LockScreenPrefs.setServiceEnabled(this, true)

            val serviceIntent = Intent(this, LockScreenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("AQOverlay", "Starting foreground service")
                startForegroundService(serviceIntent)
            } else {
                Log.d("AQOverlay", "Starting service")
                startService(serviceIntent)
            }
        }
    }
}
