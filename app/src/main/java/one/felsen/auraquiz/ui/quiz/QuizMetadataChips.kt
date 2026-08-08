package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun QuizMetadataChips(title: String) {
    val appearance = LocalQuizAppearance.current

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = {},
            enabled = false,
            label = {
                Text(
                    text = title,
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
    }
}