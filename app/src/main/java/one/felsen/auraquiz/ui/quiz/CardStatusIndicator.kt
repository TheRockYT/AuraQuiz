package one.felsen.auraquiz.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CardState(
    val label: String,
    val color: Color,
    val emoji: String
) {
    NEW(
        label = "New",
        color = Color(0xFF2196F3), // Blue
        emoji = "✨"
    ),
    LEARNING(
        label = "Learning",
        color = Color(0xFFFF9800), // Orange
        emoji = "\uD83D\uDCD6"
    ),
    RELEARNING(
        label = "Relearning",
        color = Color(0xFFF44336), // Red
        emoji = "🔄"
    ),
    REVIEW(
        label = "Review",
        color = Color(0xFF4CAF50), // Green
        emoji = "🧠"
    )
}

/**
 * A pill-style badge indicating the current card learning status.
 */
@Composable
fun CardStatusIndicator(
    state: CardState,
    modifier: Modifier = Modifier,
) {
    var isCollapsed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .background(
                color = state.color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                isCollapsed = !isCollapsed
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = state.emoji,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            AnimatedVisibility(isCollapsed) {
                Text(
                    text = state.label,
                    color = state.color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun CardStatusIndicatorPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardStatusIndicator(state = CardState.NEW)
            CardStatusIndicator(state = CardState.LEARNING)
            CardStatusIndicator(state = CardState.RELEARNING)
            CardStatusIndicator(state = CardState.REVIEW)
        }
    }
}