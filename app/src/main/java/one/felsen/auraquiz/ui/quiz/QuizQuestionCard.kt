package one.felsen.auraquiz.ui.quiz

import android.R.attr.category
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
import androidx.compose.ui.unit.dp

@Composable
fun QuizQuestionCard(
    title: String,
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
        modifier = Modifier.fillMaxWidth(),
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
            QuizMetadataChips(title)

            Text(
                text = "Aura Quiz",
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )
        }
    }
}

