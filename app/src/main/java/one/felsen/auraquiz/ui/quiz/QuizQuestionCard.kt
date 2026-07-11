package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.trivia.AppQuestion
import one.felsen.auraquiz.ui.theme.AuraQuizTheme

@Composable
fun QuizQuestionCard(
    question: String,
    category: String,
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val appearance = LocalQuizAppearance.current
    val containerColor = if (appearance.useGlassStyle) {
        appearance.cardColor
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val textColor = if (appearance.useGlassStyle) {
        appearance.contentColor
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (appearance.useGlassStyle) 0.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuizMetadataChips(
                category = category,
                difficulty = difficulty
            )

            Text(
                text = question,
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )
        }
    }
}

@Preview
@Composable
private fun QuizQuestionCardPreview() {
    AuraQuizTheme {
        QuizQuestionCard(
            question = "What is the tallest mountain in Canada?",
            category = "Geography",
            difficulty = "hard"
        )
    }
}

@Composable
fun QuizQuestionCard(
    question: AppQuestion,
    modifier: Modifier = Modifier
) {
    QuizQuestionCard(
        question = question.question,
        category = question.category,
        difficulty = question.difficulty,
        modifier = modifier
    )
}
