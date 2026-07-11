@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.12.0")
@file:DependsOn("com.google.code.gson:gson:2.10.1")
@file:DependsOn("org.apache.commons:commons-text:1.12.0")

import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import kotlin.system.exitProcess

// --- Models for OpenTDB API ---
data class TokenResponse(
    @SerializedName("response_code") val responseCode: Int,
    val token: String?
)

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaResult>?
)

data class TriviaResult(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)

// --- Cleaned Model for your Android App ---
data class AppQuestion(
    val id: Int,
    val category: String,
    val difficulty: String,
    val type: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)

val client = OkHttpClient()
val gson = GsonBuilder().setPrettyPrinting().create()

fun fetchJson(url: String): String? {
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        return if (response.isSuccessful) response.body?.string() else null
    }
}

fun requestToken(): String {
    println("Requesting new session token...")
    val url = "https://opentdb.com/api_token.php?command=request"
    val json = fetchJson(url) ?: throw Exception("Failed to fetch token")
    val response = gson.fromJson(json, TokenResponse::class.java)

    if (response.responseCode == 0 && response.token != null) {
        println("Token generated: ${response.token}")
        return response.token
    } else {
        throw Exception("Failed to generate token. Code: ${response.responseCode}")
    }
}

// Decode HTML entities (e.g. &quot; -> ") for native Android usage
fun cleanText(text: String): String = StringEscapeUtils.unescapeHtml4(text)

fun main() {
    val targetQuestionCount = 5000
    val questionsPerRequest = 50 // OpenTDB max limit per request
    val rateLimitDelayMs = 5500L // 5 seconds mandated + 500ms safety buffer

    val allQuestions = mutableListOf<AppQuestion>()
    var token = requestToken()

    var requestsMade = 0

    while (allQuestions.size < targetQuestionCount) {
        val remaining = targetQuestionCount - allQuestions.size
        val amount = if (remaining < questionsPerRequest) remaining else questionsPerRequest

        val url = "https://opentdb.com/api.php?amount=$amount&token=$token"
        println("Fetching $amount questions... (Total so far: ${allQuestions.size}/$targetQuestionCount)")

        val json = fetchJson(url)
        if (json == null) {
            println("Network error. Retrying in 5 seconds...")
            Thread.sleep(rateLimitDelayMs)
            continue
        }

        val response = gson.fromJson(json, TriviaResponse::class.java)

        when (response.responseCode) {
            0 -> {
                // Success
                response.results?.forEach { result ->
                    val appQuestion = AppQuestion(
                        id = allQuestions.size + 1,
                        category = cleanText(result.category),
                        difficulty = result.difficulty,
                        type = result.type,
                        question = cleanText(result.question),
                        correctAnswer = cleanText(result.correctAnswer),
                        incorrectAnswers = result.incorrectAnswers.map { cleanText(it) }
                    )
                    allQuestions.add(appQuestion)
                }
            }
            1 -> {
                println("Code 1: No Results. The API doesn't have enough questions left for this token.")
                break
            }
            3 -> {
                println("Code 3: Token Not Found. Requesting new token...")
                token = requestToken()
            }
            4 -> {
                println("Code 4: Token Empty. All possible questions fetched for this token. Resetting token...")
                val resetUrl = "https://opentdb.com/api_token.php?command=reset&token=$token"
                fetchJson(resetUrl)
            }
            5 -> {
                println("Code 5: Rate Limit Hit! We requested too fast. Backing off for 10 seconds...")
                Thread.sleep(10000L)
                continue
            }
            else -> {
                println("Unknown Response Code: ${response.responseCode}")
                break
            }
        }

        requestsMade++

        // Respect the 5-second per-IP rate limit
        if (allQuestions.size < targetQuestionCount) {
            println("Sleeping for ${rateLimitDelayMs}ms to respect rate limit...")
            Thread.sleep(rateLimitDelayMs)
        }
    }

    val outputFile = File("trivia_questions.json")
    outputFile.writeText(gson.toJson(allQuestions))
    println("Successfully saved ${allQuestions.size} formatted questions to ${outputFile.absolutePath}")

    // Explicitly shut down OkHttp threads to allow the script to exit immediately
    client.dispatcher.executorService.shutdown()
    client.connectionPool.evictAll()
    exitProcess(0)
}

main()
