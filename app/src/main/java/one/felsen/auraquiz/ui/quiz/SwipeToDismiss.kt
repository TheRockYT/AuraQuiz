package one.felsen.auraquiz.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun SwipeToDismissContainer(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundTapToDismiss: Boolean = false,
    scrimColor: Color = Color.Transparent,
    dismissThreshold: Dp = 96.dp,
    touchSlop: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { dismissThreshold.toPx() }
    val touchSlopPx = with(density) { touchSlop.toPx() }
    val fadeThresholdPx = thresholdPx * 2f

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (backgroundTapToDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .pointerInput(enabled, thresholdPx, touchSlopPx) {
                    if (!enabled) return@pointerInput

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var totalDrag = Offset.Zero
                        var dismissGestureActive = false

                        do {
                            val event = awaitPointerEvent(
                                if (dismissGestureActive) {
                                    PointerEventPass.Main
                                } else {
                                    PointerEventPass.Initial
                                }
                            )
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break

                            if (!change.pressed) break

                            val delta = change.positionChange()
                            if (delta != Offset.Zero) {
                                totalDrag += delta

                                if (!dismissGestureActive &&
                                    sqrt(totalDrag.x * totalDrag.x + totalDrag.y * totalDrag.y) > touchSlopPx
                                ) {
                                    dismissGestureActive = true
                                    isDragging = true
                                }

                                if (dismissGestureActive) {
                                    change.consume()
                                    scope.launch {
                                        offsetX.snapTo(
                                            totalDrag.x.coerceIn(-thresholdPx * 1.5f, thresholdPx * 1.5f)
                                        )
                                        offsetY.snapTo(
                                            totalDrag.y.coerceIn(-thresholdPx * 1.5f, thresholdPx * 1.5f)
                                        )
                                    }
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        val dragDistance = sqrt(totalDrag.x * totalDrag.x + totalDrag.y * totalDrag.y)
                        isDragging = false

                        if (dismissGestureActive && dragDistance >= thresholdPx) {
                            onDismiss()
                        } else {
                            scope.launch {
                                launch { offsetX.animateTo(0f, tween(durationMillis = 200)) }
                                launch { offsetY.animateTo(0f, tween(durationMillis = 200)) }
                            }
                        }
                    }
                }
                .graphicsLayer {
                    translationX = offsetX.value
                    translationY = offsetY.value
                    val dragDistance = sqrt(
                        offsetX.value * offsetX.value + offsetY.value * offsetY.value
                    )
                    alpha = 1f - (dragDistance / fadeThresholdPx).coerceIn(0f, 0.35f)
                }
        ) {
            content()
        }

        AnimatedVisibility(
            visible = isDragging,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(120))
        ) {
            SwipeDismissHint()
        }
    }
}

@Composable
fun SwipeDismissHint(
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified
) {
    val appearance = LocalQuizAppearance.current
    val color = if (textColor != Color.Unspecified) {
        textColor
    } else if (appearance.useGlassStyle) {
        appearance.contentColor.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (appearance.useGlassStyle) {
            Color.Black.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
    ) {
        Text(
            text = "Swipe to exit",
            style = MaterialTheme.typography.labelLarge,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}
