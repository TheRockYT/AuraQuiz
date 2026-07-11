package one.felsen.auraquiz.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.ui.theme.AuraQuizTheme
import one.felsen.auraquiz.ui.theme.QuizCorrect
import one.felsen.auraquiz.ui.theme.QuizCorrectContainer
import one.felsen.auraquiz.ui.theme.QuizIncorrect
import one.felsen.auraquiz.ui.theme.QuizIncorrectContainer

@Composable
fun QuizAnswerOption(
    label: String,
    text: String,
    state: QuizAnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = state.containerColor(),
        animationSpec = tween(durationMillis = 250),
        label = "answerContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = state.contentColor(),
        animationSpec = tween(durationMillis = 250),
        label = "answerContentColor"
    )
    val borderColor by animateColorAsState(
        targetValue = state.borderColor(),
        animationSpec = tween(durationMillis = 250),
        label = "answerBorderColor"
    )

    Card(
        onClick = onClick,
        enabled = state.isClickable(),
        modifier = modifier
            .fillMaxWidth()
            .semantics { role = Role.RadioButton },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (state == QuizAnswerState.Default) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = state.badgeColor(),
                contentColor = state.badgeContentColor()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuizAnswerState.containerColor(): Color {
    val appearance = LocalQuizAppearance.current
    return when (this) {
        QuizAnswerState.Default -> if (appearance.useGlassStyle) {
            appearance.answerColor
        } else {
            MaterialTheme.colorScheme.surface
        }
        QuizAnswerState.Selected -> MaterialTheme.colorScheme.primaryContainer
        QuizAnswerState.Correct -> QuizCorrectContainer
        QuizAnswerState.Incorrect -> QuizIncorrectContainer
        QuizAnswerState.Disabled -> if (appearance.useGlassStyle) {
            Color.White.copy(alpha = 0.06f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        }
    }
}

@Composable
private fun QuizAnswerState.contentColor(): Color {
    val appearance = LocalQuizAppearance.current
    return when (this) {
        QuizAnswerState.Default -> if (appearance.useGlassStyle) {
            appearance.contentColor
        } else {
            MaterialTheme.colorScheme.onSurface
        }
        QuizAnswerState.Selected -> MaterialTheme.colorScheme.onPrimaryContainer
        QuizAnswerState.Correct -> QuizCorrect
        QuizAnswerState.Incorrect -> QuizIncorrect
        QuizAnswerState.Disabled -> if (appearance.useGlassStyle) {
            appearance.contentColor.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}

@Composable
private fun QuizAnswerState.borderColor(): Color {
    val appearance = LocalQuizAppearance.current
    return when (this) {
        QuizAnswerState.Default -> if (appearance.useGlassStyle) {
            appearance.answerBorderColor
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }
        QuizAnswerState.Selected -> MaterialTheme.colorScheme.primary
        QuizAnswerState.Correct -> QuizCorrect
        QuizAnswerState.Incorrect -> QuizIncorrect
        QuizAnswerState.Disabled -> if (appearance.useGlassStyle) {
            appearance.answerBorderColor.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        }
    }
}

@Composable
private fun QuizAnswerState.badgeColor(): Color {
    val appearance = LocalQuizAppearance.current
    return when (this) {
        QuizAnswerState.Default -> if (appearance.useGlassStyle) {
            Color.White.copy(alpha = 0.18f)
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
        QuizAnswerState.Selected -> MaterialTheme.colorScheme.primary
        QuizAnswerState.Correct -> QuizCorrect
        QuizAnswerState.Incorrect -> QuizIncorrect
        QuizAnswerState.Disabled -> if (appearance.useGlassStyle) {
            Color.White.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    }
}

@Composable
private fun QuizAnswerState.badgeContentColor(): Color {
    val appearance = LocalQuizAppearance.current
    return when (this) {
        QuizAnswerState.Default -> if (appearance.useGlassStyle) {
            appearance.contentColor
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }
        QuizAnswerState.Selected -> MaterialTheme.colorScheme.onPrimary
        QuizAnswerState.Correct -> Color.White
        QuizAnswerState.Incorrect -> Color.White
        QuizAnswerState.Disabled -> if (appearance.useGlassStyle) {
            appearance.contentColor.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}

private fun QuizAnswerState.isClickable(): Boolean =
    this == QuizAnswerState.Default || this == QuizAnswerState.Selected

@Preview
@Composable
private fun QuizAnswerOptionPreview() {
    AuraQuizTheme {
        QuizAnswerOption(
            label = "A",
            text = "Mount Logan",
            state = QuizAnswerState.Default,
            onClick = {}
        )
    }
}
