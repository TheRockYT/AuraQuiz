package one.felsen.auraquiz.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.ui.theme.QuizCorrect
import one.felsen.auraquiz.ui.theme.QuizCorrectContainer
import one.felsen.auraquiz.ui.theme.QuizIncorrect
import one.felsen.auraquiz.ui.theme.QuizIncorrectContainer

@Composable
fun QuizFeedbackBanner(
    isSolved: Boolean,
    hasWrongAttempt: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isSolved || hasWrongAttempt,
        modifier = modifier,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 }
    ) {
        val (containerColor, contentColor, message) = when {
            isSolved -> Triple(
                QuizCorrectContainer,
                QuizCorrect,
                "Correct! Well done."
            )
            else -> Triple(
                QuizIncorrectContainer,
                QuizIncorrect,
                "Not quite. Try another answer."
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = containerColor,
            contentColor = contentColor
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}
