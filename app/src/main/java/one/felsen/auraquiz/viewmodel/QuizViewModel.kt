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
import one.felsen.auraquiz.data.card.ReviewLogEntity
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
            val card = data.card
            val cardId = card.id
            val cardData = data.cardData

            val now = Clock.System.now()
            val nowEpoch = now.toEpochMilliseconds()
            val lastReview = Instant.fromEpochMilliseconds(cardData?.lastReview ?: nowEpoch)
            val elapsedDays = now.elapsedDays(lastReview)

            val fsrsState = cardData?.let { FsrsState(difficulty = it.difficulty, stability = it.stability) }

            val calc = FsrsCalculator()
            val review = calc.review(
                state = fsrsState,
                rating = rating,
                elapsedDays = elapsedDays,
                sameDay = lastReview.isSameDay(now)
            )

            val reviewStability = review.stability
            val reviewDifficulty = review.difficulty

            val stabilityDays = reviewStability.days
            val dueDate = now.plus(stabilityDays).toEpochMilliseconds()

            val newCardData = cardData?.copy(
                dueDate = dueDate,
                lastReview = nowEpoch,
                difficulty = reviewDifficulty,
                stability = reviewStability,
                updatedTimestamp = nowEpoch
            )
                ?: CardDataEntity(
                    id = cardId,
                    dueDate = dueDate,
                    lastReview = nowEpoch,
                    difficulty = reviewDifficulty,
                    stability = reviewStability,
                    creationTimestamp = nowEpoch,
                    updatedTimestamp = nowEpoch
                )

            try {

                cardRepository.upsertCardData(
                    cardDataEntity = newCardData
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred while updating card data")
                return@launch
            }

            try {
                cardRepository.insertReviewLogIgnore(
                    ReviewLogEntity(
                        cardId = cardId,
                        reviewedAt = nowEpoch,
                        rating = rating.value,
                        stability = reviewStability,
                        difficulty = reviewDifficulty,
                        elapsedDays = elapsedDays,
                        scheduledDays = reviewStability
                    )
                )
            } catch (e: Exception) {
                _uiState.value =
                    UiState.Error(e.localizedMessage ?: "Unknown error occurred while inserting review log")
                return@launch
            }

            nextCard()
        }
    }
}
