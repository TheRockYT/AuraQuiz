package one.felsen.auraquiz.viewmodel

import one.felsen.auraquiz.data.card.CardEntity
import kotlin.uuid.Uuid

sealed interface CardUpsertState {
    data class EDITING(val initialCard: CardEntity) : CardUpsertState
    data object CREATING : CardUpsertState
    data class SAVED(val uuid: Uuid) : CardUpsertState
    data class DELETED(val uuid: Uuid) : CardUpsertState
}
