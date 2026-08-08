package one.felsen.auraquiz.ui.quiz

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun QuizAnswerOption(
    front: String,
    back: String,
) {
    val appearance = LocalQuizAppearance.current

    val containerColor = if (appearance.useGlassStyle) {
        appearance.answerColor
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (appearance.useGlassStyle) {
        appearance.contentColor
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (appearance.useGlassStyle) {
        appearance.answerBorderColor
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    key(front, back) {

        // State to track if the card is flipped
        var isFlipped by remember { mutableStateOf(false) }

        // Animate the rotation angle from 0 to 180 degrees
        val rotation by animateFloatAsState(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "flashcard_flip"
        )


        // Determine if we are looking at the front or back (swaps at 90 degrees)
        val isFront = rotation <= 90f

        Card(
            onClick = { isFlipped = !isFlipped },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Apply the Y-axis rotation
                    rotationY = rotation
                    // Set camera distance to give a 3D perspective/depth effect
                    cameraDistance = 12f * density
                },
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor,
                disabledContentColor = contentColor
            ),
            border = BorderStroke(1.5.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .graphicsLayer {
                        // Prevent the back text from being completely mirrored
                        if (!isFront) {
                            rotationY = 180f
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    // Swap the text depending on which side is showing
                    text = if (isFront) front else back,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
