package one.felsen.auraquiz.ui.quiz

import android.R.attr.maxHeight
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardData
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.viewmodel.DeckDetailsViewModel
import one.felsen.auraquiz.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    cardRepository: CardRepository,
    appearance: QuizAppearance = QuizAppearance.Default,
    onDismiss: (() -> Unit) = {},
    onLockScreen: Boolean = false
) {

    val deckDetailsViewModel = viewModel { QuizViewModel(cardRepository) }
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
                    when(val state = uiState) {
                        is UiState.Error -> {
                            ErrorDialog(
                                message = state.message, onDismissRequest = { onDismiss() }
                            )
                        }
                        is UiState.Loading -> {
                            LoadingDialog()
                        }
                        is UiState.Success -> {
                            val card = state.data
                            QuizQuestionCard(card.title)

                            Column(
                                modifier = Modifier.fillMaxSize().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                if (card.data is CardData.Flashcard) {
                                    QuizAnswerOption(
                                        front = card.data.front,
                                        back = card.data.back,
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
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RatingButton(
                                    text = "Again",
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                                RatingButton(
                                    text = "Hard",
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                RatingButton(
                                    text = "Middle",
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                RatingButton(
                                    text = "Good",
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        // Allows buttons to scale nicely if screen size is tight
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}