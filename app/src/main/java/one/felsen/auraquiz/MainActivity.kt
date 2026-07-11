package one.felsen.auraquiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import one.felsen.auraquiz.lock.LockScreenPrefs
import one.felsen.auraquiz.lock.LockScreenService
import one.felsen.auraquiz.trivia.TriviaRepository
import one.felsen.auraquiz.ui.quiz.QuizScreen
import one.felsen.auraquiz.ui.theme.AuraQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AuraQuizTheme {
                val isRunning by LockScreenService.isServiceRunning.collectAsState()
                var currentQuestion by remember {
                    mutableStateOf(TriviaRepository.getNextRandomQuestion(this@MainActivity))
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isRunning) "Lock screen: Active" else "Lock screen: Inactive",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Button(
                                onClick = {
                                    if (isRunning) {
                                        stopLockService()
                                    } else {
                                        checkPermissionAndStartService()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isRunning) "Stop Lock Screen" else "Enable Lock Screen")
                            }
                        }

                        HorizontalDivider()

                        currentQuestion?.let { question ->
                            QuizScreen(
                                question = question,
                                modifier = Modifier.weight(1f),
                                onContinue = {
                                    currentQuestion = TriviaRepository.getNextRandomQuestion(
                                        this@MainActivity
                                    )
                                }
                            )
                        } ?: Text(
                            text = "No questions available",
                            modifier = Modifier.padding(20.dp)
                        )
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
