package one.felsen.auraquiz.trivia

import kotlinx.serialization.Serializable
import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class AppQuestion(
    val id: Int,
    val category: String,
    val difficulty: String,
    val type: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)



@OptIn(ExperimentalSerializationApi::class)
fun loadQuestionsFromAssets(context: Context): List<AppQuestion> {
    // .use {} ensures the InputStream is automatically closed to prevent memory leaks
    return context.assets.open("trivia_questions.json").use { inputStream ->
        Json.decodeFromStream(inputStream)
    }
}
