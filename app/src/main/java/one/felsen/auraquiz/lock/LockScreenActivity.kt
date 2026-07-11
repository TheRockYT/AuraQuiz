package one.felsen.auraquiz.lock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.trivia.TriviaRepository
import one.felsen.auraquiz.ui.quiz.QuizAppearance
import one.felsen.auraquiz.ui.quiz.QuizScreen
import one.felsen.auraquiz.ui.theme.AuraQuizTheme

class LockScreenActivity : ComponentActivity() {

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes.blurBehindRadius = 50
        }

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        setContent {
            AuraQuizTheme {
                var currentQuestion by remember {
                    mutableStateOf(TriviaRepository.getNextRandomQuestion(this@LockScreenActivity))
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x66000000)),
                    contentAlignment = Alignment.Center
                ) {
                    currentQuestion?.let { question ->
                        QuizScreen(
                            question = question,
                            modifier = Modifier.fillMaxSize(),
                            appearance = QuizAppearance.LockScreen,
                            onContinue = {
                                currentQuestion = TriviaRepository.getNextRandomQuestion(
                                    this@LockScreenActivity
                                )
                            },
                            autoAdvanceOnCorrect = true,
                            showContinueButton = false,
                            onDismiss = { finish() },
                            dismissButtonLabel = "Back to Lock Screen",
                            contentMaxWidth = 560.dp
                        )
                    } ?: Text(
                        text = "No questions available",
                        color = Color.White,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOffReceiver)
    }
}
