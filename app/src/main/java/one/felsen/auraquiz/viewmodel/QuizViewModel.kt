package one.felsen.auraquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import one.felsen.auraquiz.data.card.CardDataEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.card.CardWithData
import one.felsen.auraquiz.settings.SettingsRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.fsrskt.fsrs6.FsrsCalculator
import one.felsen.fsrskt.fsrs6.FsrsRating
import one.felsen.fsrskt.fsrs6.FsrsState
import one.felsen.fsrskt.helper.DateTimeHelper.elapsedDays
import one.felsen.fsrskt.helper.DateTimeHelper.isSameDay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class QuizViewModel(private val cardRepository: CardRepository, private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CardWithData>>(UiState.Loading)
    val uiState: StateFlow<UiState<CardWithData>> = _uiState.asStateFlow()

    private var updateJob: Job? = null

    init {
        nextCard()
        startUpdate()
    }

    private fun startUpdate(fast: Boolean = false) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            if (fast) {
                delay(5.seconds)
            } else {
                delay(20.seconds)
            }
            nextCard()
        }
    }

    fun nextCard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextCard = cardRepository.getNextCardToStudy(settingsMax = settingsRepository.getMaxNew().first())
                if (nextCard != null) {
                    _uiState.value = UiState.Success(nextCard)
                    startUpdate(false)
                } else {
                    _uiState.value = UiState.Error("No cards available for study.")
                    startUpdate(true)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
                startUpdate(true)
            }
        }
    }

    fun rateCard(rating: FsrsRating) {
        viewModelScope.launch {

            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                _uiState.value = UiState.Error("Cannot rate card: Not in a success state")
                return@launch
            }

            val data = stateValue.data
            val cardData = data.cardData

            val now = Clock.System.now()
            val lastReview = Instant.fromEpochMilliseconds(cardData?.lastReview ?: now.toEpochMilliseconds())

            val fsrsState = cardData?.let { FsrsState(difficulty = it.difficulty, stability = it.stability) }

            val calc = FsrsCalculator()
            val review = calc.review(
                state = fsrsState, rating = rating, lastReview.elapsedDays(now),
                lastReview.isSameDay(now)
            )

            val newCardData = cardData?.copy(
                dueDate = now.plus(review.stability.days).toEpochMilliseconds(),
                lastReview = now.toEpochMilliseconds(),
                difficulty = review.difficulty,
                stability = review.stability,
                updatedTimestamp = now.toEpochMilliseconds()
            )
                ?: CardDataEntity(
                    id = data.card.id,
                    dueDate = now.plus(review.stability.days).toEpochMilliseconds(),
                    lastReview = now.toEpochMilliseconds(),
                    difficulty = review.difficulty,
                    stability = review.stability,
                    creationTimestamp = now.toEpochMilliseconds(),
                    updatedTimestamp = now.toEpochMilliseconds()
                )

            try {

                cardRepository.upsertCardData(
                    cardDataEntity = newCardData
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
                return@launch
            }

            nextCard()
        }
    }
}
