package one.felsen.auraquiz.viewmodel

import one.felsen.auraquiz.data.deck.DeckEntity
import kotlin.uuid.Uuid

sealed interface DeckUpsertState {
    data class EDITING(val initialDeck: DeckEntity) : DeckUpsertState
    data object CREATING : DeckUpsertState
    data class SAVED(val uuid: Uuid) : DeckUpsertState
    data class DELETED(val uuid: Uuid) : DeckUpsertState
}
