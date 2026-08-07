package one.felsen.auraquiz.data.card

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the data of a card in the quiz application.
 * This sealed interface allows for different types of card data, each with its own structure.
 * The specific card types are defined as data classes that implement this interface.
 * Each card type should be suffixed with a version number at the end to allow for future changes in the data structure without breaking existing data.
 * The @Serializable annotation allows for serialization and deserialization of the card data.
 */
@Serializable
sealed interface CardData {
    @Serializable
    @SerialName("flashcard_1")
    data class Flashcard(val front: String, val back: String) : CardData

    @Serializable
    @SerialName("sentence_builder_1")
    data class SentenceBuilder(val sequence: List<String>, val distractor: List<String>) : CardData

    @Serializable
    @SerialName("note_1")
    data class Note(val content: String) : CardData

    @Serializable
    @SerialName("multiple_choice_1")
    data class MultipleChoice(val question: String, val answer: String, val distractorAnswers: List<String>) : CardData

    @Serializable
    @SerialName("binary_choice_1")
    data class BinaryChoice(val statement: String, val isTrue: Boolean) : CardData

    @Serializable
    @SerialName("fill_in_the_blank_1")
    data class FillInTheBlank(val tokens: List<Token>, val distractorTokens: List<String>) : CardData {
        @Serializable
        data class Token(val text: String, val isBlank: Boolean = false, val attachToNext: Boolean = false)
    }

    @Serializable
    @SerialName("matching_pairs_1")
    data class MatchingPairs(val pairs: Map<String, String>) : CardData
}
