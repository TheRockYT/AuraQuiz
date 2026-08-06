package one.felsen.auraquiz.data.card

import androidx.room3.Embedded
import one.felsen.auraquiz.data.deck.DeckEntity

data class DeckWithStats(
    @Embedded val deck: DeckEntity,
    val availableNewCount: Int,     // Total untouched cards in deck
    val todayNewCount: Int,         // New cards seen today
    val newForNow: Int,             // New cards available *today* (respecting daily limit)
    val dueForNow: Int              // Review cards due right now
)
