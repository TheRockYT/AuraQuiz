package one.felsen.auraquiz.trivia

import android.content.Context

object TriviaRepository {
    
    private val questionDeck = mutableListOf<AppQuestion>()

    private fun initialize(context: Context) {
        if (questionDeck.isEmpty()) {
            // Load the questions and immediately shuffle the "deck"
            val allQuestions = loadQuestionsFromAssets(context)
            questionDeck.addAll(allQuestions.shuffled())
        }
    }

    fun getNextRandomQuestion(context: Context): AppQuestion? {
        if (questionDeck.isEmpty()) {
            // If the deck is empty, load the questions and shuffle them
            initialize(context)
        }

        return questionDeck.removeLastOrNull()
    }
}
