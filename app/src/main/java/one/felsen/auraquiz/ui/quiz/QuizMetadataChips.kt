package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.ui.theme.QuizEasy
import one.felsen.auraquiz.ui.theme.QuizHard
import one.felsen.auraquiz.ui.theme.QuizMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizMetadataChips(
    category: String,
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val appearance = LocalQuizAppearance.current

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = {},
            enabled = false,
            label = {
                Text(
                    text = category,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                disabledContainerColor = if (appearance.useGlassStyle) {
                    Color.White.copy(alpha = 0.14f)
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                disabledLabelColor = if (appearance.useGlassStyle) {
                    appearance.contentColor
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            )
        )

        val difficultyColor = difficultyColor(difficulty)
        AssistChip(
            onClick = {},
            enabled = false,
            label = {
                Text(
                    text = difficulty.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                disabledContainerColor = difficultyColor.copy(alpha = 0.18f),
                disabledLabelColor = difficultyColor
            )
        )
    }
}

private fun difficultyColor(difficulty: String): Color = when (difficulty.lowercase()) {
    "easy" -> QuizEasy
    "hard" -> QuizHard
    else -> QuizMedium
}
