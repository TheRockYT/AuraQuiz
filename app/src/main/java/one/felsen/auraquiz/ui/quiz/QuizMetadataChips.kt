package one.felsen.auraquiz.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun QuizMetadataChips(hint: String? = null, explanation: String? = null) {
    val appearance = LocalQuizAppearance.current

    var showHint by remember { mutableStateOf(false) }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        if (!hint.isNullOrBlank()) {
            AssistChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(
                        text = hint,
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
        Spacer(modifier = Modifier.weight(1f))
        if(!explanation.isNullOrBlank()) {
            IconButton(
                onClick = { showHint = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = "Show explanation",
                    tint = if (appearance.useGlassStyle) {
                        appearance.contentColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            if (showHint) {
                AlertDialog(
                    onDismissRequest = { showHint = false },
                    confirmButton = {},
                    text = { Text(explanation) }
                )
            }
        }
    }
}
