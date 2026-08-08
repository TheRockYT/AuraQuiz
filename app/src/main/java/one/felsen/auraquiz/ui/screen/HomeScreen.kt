package one.felsen.auraquiz.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.settings.SettingsRepository
import one.felsen.auraquiz.ui.quiz.QuizScreen

@Composable
fun HomeScreen(onOpenSettings: () -> Unit, cardRepository: CardRepository, settingsRepository: SettingsRepository) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("AuraQuiz") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            QuizScreen(
                cardRepository = cardRepository,
                settingsRepository = settingsRepository
            )
        }
    }
}