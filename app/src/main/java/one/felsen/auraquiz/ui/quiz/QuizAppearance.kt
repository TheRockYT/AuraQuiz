package one.felsen.auraquiz.ui.quiz

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class QuizAppearance(
    val backgroundColor: Color? = null,
    val useGlassStyle: Boolean = false,
    val contentColor: Color = Color.Unspecified,
    val cardColor: Color = Color.Unspecified,
    val answerColor: Color = Color.Unspecified,
    val answerBorderColor: Color = Color.Unspecified,
    val horizontalPadding: Dp = 20.dp,
    val verticalPadding: Dp = 24.dp,
) {
    companion object {
        val Default = QuizAppearance()

        val LockScreen = QuizAppearance(
            backgroundColor = Color.Transparent,
            useGlassStyle = true,
            contentColor = Color.White,
            cardColor = Color.White.copy(alpha = 0.12f),
            answerColor = Color.White.copy(alpha = 0.10f),
            answerBorderColor = Color.White.copy(alpha = 0.28f),
            horizontalPadding = 28.dp,
            verticalPadding = 36.dp
        )
    }
}

val LocalQuizAppearance = compositionLocalOf { QuizAppearance.Default }
