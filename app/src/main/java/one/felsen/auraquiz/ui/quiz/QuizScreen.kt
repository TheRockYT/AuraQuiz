package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardData
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.settings.SettingsRepository
import one.felsen.auraquiz.ui.UiState
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RatingButton(
                                text = "Again",
                                icon = Icons.Default.Refresh,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF475569), Color(0xFF334155))
                                ),
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.AGAIN) }
                            )
                            RatingButton(
                                text = "Hard",
                                icon = Icons.Default.Warning,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF97316), Color(0xFFEA580C))
                                ),
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.HARD) }
                            )
                            RatingButton(
                                text = "Good",
                                icon = Icons.Default.ThumbUp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                ),
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
                                onClick = { deckDetailsViewModel.rateCard(FsrsRating.GOOD) }
                            )
                            RatingButton(
                                text = "Easy",
                                icon = Icons.Default.Star,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF10B981), Color(0xFF047857))
                                ),
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
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
    brush: Brush,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}