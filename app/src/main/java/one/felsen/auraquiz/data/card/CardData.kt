package one.felsen.auraquiz.data.card

import kotlinx.serialization.Serializable

@Serializable
sealed interface CardData {
    @Serializable
    data class Flashcard(val front: String, val back: String) : CardData

    @Serializable
    data class SentenceBuilder(val sequence: List<String>, val distractor: List<String>) : CardData

    @Serializable
    data class Note(val content: String) : CardData

    @Serializable
    data class MultipleChoice(val question: String, val answer: String, val wrongAnswers: List<String>) : CardData

    @Serializable
    data class BinaryChoice(val statement: String, val isTrue: Boolean) : CardData

    @Serializable
    data class FillInTheBlank(val templateText: String, val correctWord: String, val wrongWords: List<String>) : CardData

    @Serializable
    data class MatchingPairs(val pairs: Map<String, String>) : CardData
}
