package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizAnswerList(
    choices: List<String>,
    correctAnswer: String,
    wrongAnswers: Set<String>,
    isSolved: Boolean,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        choices.forEachIndexed { index, choice ->
            val label = remember(index) { ('A' + index).toString() }
            val state = when {
                isSolved && choice == correctAnswer -> QuizAnswerState.Correct
                isSolved -> QuizAnswerState.Disabled
                choice in wrongAnswers -> QuizAnswerState.Incorrect
                else -> QuizAnswerState.Default
            }

            QuizAnswerOption(
                label = label,
                text = choice,
                state = state,
                onClick = { onAnswerSelected(choice) }
            )
        }
    }
}
