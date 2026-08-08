package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardData
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.settings.SettingsRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.viewmodel.QuizViewModel
import one.felsen.fsrskt.fsrs6.FsrsRating

@Composable
fun QuizScreen(
    cardRepository: CardRepository,
    settingsRepository: SettingsRepository,
    appearance: QuizAppearance = QuizAppearance.Default,
    onDismiss: (() -> Unit) = {},
    onLockScreen: Boolean = false
) {

    val deckDetailsViewModel = viewModel { QuizViewModel(cardRepository, settingsRepository) }
    val uiState by deckDetailsViewModel.uiState.collectAsStateWithLifecycle()

    val backgroundColor = appearance.backgroundColor ?: MaterialTheme.colorScheme.background
    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalQuizAppearance provides appearance) {
        val quizContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(WindowInsets.safeDrawing.asPaddingValues())
                    .padding(
                        horizontal = appearance.horizontalPadding,
                        vertical = appearance.verticalPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (val state = uiState) {
                    is UiState.Error -> {
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    is UiState.Loading -> {
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Please wait...",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    is UiState.Success -> {
                        val cardWithData = state.data
                        QuizQuestionCard(cardWithData.card.title)

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            if (cardWithData.card.data is CardData.Flashcard) {
                                QuizAnswerOption(
                                    front = cardWithData.card.data.front,
                                    back = cardWithData.card.data.back,
                                )
                            } else {
                                QuizAnswerOption(
                                    front = "Currently unsupported card type",
                                    back = "Please check back later",
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp), // Small padding on the edges of the screen
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Even gaps between buttons
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RatingButton(
                                text = "Again",
                                modifier = Modifier.weight(1f), // Takes up exactly 25% of the space
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.AGAIN) }
                            )
                            RatingButton(
                                text = "Hard",
                                modifier = Modifier.weight(1f),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.HARD) }
                            )
                            RatingButton(
                                text = "Good",
                                modifier = Modifier.weight(1f),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.GOOD) }
                            )
                            RatingButton(
                                text = "Easy",
                                modifier = Modifier.weight(1f),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.EASY) }
                            )
                        }
                    }
                }
            }
        }

        if (onLockScreen) {
            SwipeToDismissContainer(
                onDismiss = onDismiss,
                enabled = true,
                backgroundTapToDismiss = true,
                scrimColor = Color(0x66000000),
                content = quizContent
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                SwipeToDismissContainer(
                    onDismiss = onDismiss,
                    enabled = false,
                    content = quizContent
                )
            }
        }
    }
}

@Composable
fun RatingButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier, // Added modifier parameter
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,      // Prevents text from stacking vertically
            softWrap = false   // Disables wrapping entirely
        )
    }
}