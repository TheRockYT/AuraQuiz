package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import one.felsen.auraquiz.trivia.AppQuestion
import one.felsen.auraquiz.ui.theme.AuraQuizTheme

@Composable
fun QuizScreen(
    question: AppQuestion,
    modifier: Modifier = Modifier,
    appearance: QuizAppearance = QuizAppearance.Default,
    onCorrectAnswer: (() -> Unit)? = null,
    onContinue: (() -> Unit)? = null,
    showContinueButton: Boolean = true,
    autoAdvanceOnCorrect: Boolean = false,
    advanceDelayMillis: Long = 700,
    onDismiss: (() -> Unit)? = null,
    dismissButtonLabel: String = "Back to Lock Screen",
    contentMaxWidth: androidx.compose.ui.unit.Dp = 640.dp
) {
    val choices = remember(question.id) { question.shuffledChoices() }
    var wrongAnswers by rememberSaveable(question.id) { mutableStateOf(setOf<String>()) }
    var isSolved by rememberSaveable(question.id) { mutableStateOf(false) }

    LaunchedEffect(question.id) {
        wrongAnswers = emptySet()
        isSolved = false
    }

    LaunchedEffect(isSolved, question.id) {
        if (isSolved && autoAdvanceOnCorrect && onContinue != null) {
            delay(advanceDelayMillis)
            onContinue()
        }
    }

    val backgroundColor = appearance.backgroundColor ?: MaterialTheme.colorScheme.background
    val scrollState = rememberScrollState()
    val swipeToDismissEnabled = onDismiss != null

    CompositionLocalProvider(LocalQuizAppearance provides appearance) {
        val quizContent: @Composable () -> Unit = {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (swipeToDismissEnabled) {
                            Modifier.wrapContentHeight()
                        } else {
                            Modifier.fillMaxSize()
                        }
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = contentMaxWidth)
                        .fillMaxWidth()
                        .then(
                            if (swipeToDismissEnabled) {
                                Modifier.heightIn(max = maxHeight)
                            } else {
                                Modifier.fillMaxSize()
                            }
                        )
                        .verticalScroll(scrollState)
                        .padding(WindowInsets.safeDrawing.asPaddingValues())
                        .padding(
                            horizontal = appearance.horizontalPadding,
                            vertical = appearance.verticalPadding
                        ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        QuizQuestionCard(question = question)

                        QuizFeedbackBanner(
                            isSolved = isSolved,
                            hasWrongAttempt = wrongAnswers.isNotEmpty() && !isSolved
                        )

                        QuizAnswerList(
                            choices = choices,
                            correctAnswer = question.correctAnswer,
                            wrongAnswers = wrongAnswers,
                            isSolved = isSolved,
                            onAnswerSelected = { selected ->
                                if (isSolved || selected in wrongAnswers) return@QuizAnswerList

                                if (selected == question.correctAnswer) {
                                    isSolved = true
                                    onCorrectAnswer?.invoke()
                                } else {
                                    wrongAnswers = wrongAnswers + selected
                                }
                            }
                        )

                        if (showContinueButton && isSolved && onContinue != null && !autoAdvanceOnCorrect) {
                            Button(
                                onClick = onContinue,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Next Question")
                            }
                        }

                        if (onDismiss != null) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                colors = if (appearance.useGlassStyle) {
                                    ButtonDefaults.outlinedButtonColors(
                                        contentColor = appearance.contentColor
                                    )
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                },
                                border = if (appearance.useGlassStyle) {
                                    BorderStroke(1.dp, appearance.answerBorderColor)
                                } else {
                                    ButtonDefaults.outlinedButtonBorder
                                }
                            ) {
                                Text(dismissButtonLabel)
                            }
                        }
                }
            }
        }

        if (swipeToDismissEnabled) {
            SwipeToDismissContainer(
                onDismiss = onDismiss,
                modifier = modifier.fillMaxSize(),
                enabled = true,
                backgroundTapToDismiss = true,
                scrimColor = Color(0x66000000),
                content = quizContent
            )
        } else {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                SwipeToDismissContainer(
                    onDismiss = { onDismiss?.invoke() },
                    enabled = false,
                    content = quizContent
                )
            }
        }
    }
}

@PreviewScreenSizes
@Preview(showBackground = true)
@Composable
private fun QuizScreenPreview() {
    AuraQuizTheme {
        QuizScreen(
            question = AppQuestion(
                id = 1,
                category = "Geography",
                difficulty = "hard",
                type = "multiple",
                question = "What is the tallest mountain in Canada?",
                correctAnswer = "Mount Logan",
                incorrectAnswers = listOf(
                    "Mont Tremblant",
                    "Whistler Mountain",
                    "Blue Mountain"
                )
            ),
            onContinue = {}
        )
    }
}
