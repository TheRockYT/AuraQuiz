"""
auraquiz.py
===========

A small, hand-written Python library for reading and writing AuraQuiz
flashcard export files (the JSON format described by
``one.felsen.auraquiz.data.ExportFile``).

This is *not* a generic JSON-Schema parser. The schema only changes
occasionally, so the shape of the data is modeled directly as plain
Python dataclasses. When the schema changes, update the matching
dataclass(es) below -- see "Updating this library when the schema
changes" at the bottom of this file.

Quick start
-----------

    import auraquiz as aq

    # --- Build a new export from scratch ---
    export = aq.ExportFile()
    deck = export.new_deck("Spanish Basics", description="Greetings & numbers")

    deck.add_flashcard(front="Hola", back="Hello")
    deck.add_multiple_choice(
        question="How do you say 'thank you'?",
        answer="Gracias",
        distractors=["Adios", "Por favor", "De nada"],
    )
    deck.add_binary_choice(statement="'Gato' means 'dog'.", is_true=False)

    aq.save(export, "spanish_basics.json")

    # --- Read an existing export back in ---
    export = aq.load("spanish_basics.json")
    for deck in export.decks:
        print(deck.name, len(deck.cards), "cards")
        for card in deck.cards:
            print(" -", card.data)
"""

from __future__ import annotations

import json
import time
import uuid
from dataclasses import dataclass, field, fields
from pathlib import Path
from typing import Any, Dict, List, Optional, Union


# ---------------------------------------------------------------------------
# helpers
# ---------------------------------------------------------------------------

def now_ms() -> int:
    """Current time in epoch milliseconds -- handy for the *Timestamp fields."""
    return int(time.time() * 1000)


def new_id() -> str:
    """A fresh random id, suitable for the ``id`` field on decks/cards."""
    return str(uuid.uuid4())


# A fixed, arbitrary namespace UUID for this library. Do not change this --
# changing it would change every id that new_id_v5() has ever produced.
NAMESPACE_AURAQUIZ = uuid.UUID("6f2b6a6e-6e6b-4f0a-9a8b-6c9a1a9f2a11")

_ID_PART_SEP = "\x1f"  # unit separator: avoids "a"+"bc" colliding with "ab"+"c"


def new_id_v5(*parts: Any, namespace: uuid.UUID = NAMESPACE_AURAQUIZ) -> str:
    """
    A *deterministic* id: the same ``parts`` always produce the same id
    (a UUIDv5), instead of a fresh random one each time.

    This is handy when re-running an import/export of the same source
    material (e.g. a spreadsheet, a markdown file, a scraped word list)
    should produce the same deck/card ids every time, rather than new
    random ids that would look like brand-new decks/cards to the app.

    Usage:

        deck = export.new_deck("Spanish Basics", id=aq.new_id_v5("deck", "Spanish Basics"))
        deck.add_flashcard(
            front="Hola", back="Hello",
            id=aq.new_id_v5("Spanish Basics", "Hola", "Hello"),
        )

    Pass enough parts to uniquely identify the thing (e.g. deck name +
    card front/back) so two different cards never accidentally hash to
    the same id. Pass a custom ``namespace`` if you want ids from this
    library to never collide with UUIDv5 ids generated elsewhere.
    """
    name = _ID_PART_SEP.join(str(p) for p in parts)
    return str(uuid.uuid5(namespace, name))


def _omit_none(d: dict) -> dict:
    """Drop keys whose value is None, so we don't emit clutter we don't need to."""
    return {k: v for k, v in d.items() if v is not None}


# ---------------------------------------------------------------------------
# Card data ("oneOf" variants of one.felsen.auraquiz.data.card.CardData)
# ---------------------------------------------------------------------------

class CardData:
    """
    Base class for all card content types.

    Every concrete subclass sets a class-level ``TYPE`` string that matches
    the discriminator used in the JSON ("binary_choice_1", "flashcard_1",
    etc). Subclasses are auto-registered so that ``CardData.parse(...)``
    can figure out which class to build from raw JSON.
    """

    TYPE: str = ""
    _registry: Dict[str, type] = {}

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        if cls.TYPE:
            CardData._registry[cls.TYPE] = cls

    def to_dict(self) -> dict:
        raise NotImplementedError

    @classmethod
    def from_dict(cls, d: dict) -> "CardData":
        raise NotImplementedError

    @staticmethod
    def parse(d: dict) -> "CardData":
        card_type = d.get("type")
        subclass = CardData._registry.get(card_type)
        if subclass is None:
            raise ValueError(
                f"Unknown card data type {card_type!r}. "
                f"Known types: {sorted(CardData._registry)}"
            )
        return subclass.from_dict(d)


@dataclass
class Token:
    """One token of a fill_in_the_blank_1 card (either shown text or a blank)."""

    text: str
    is_blank: Optional[bool] = None
    attach_to_next: Optional[bool] = None

    def to_dict(self) -> dict:
        return _omit_none(
            {
                "text": self.text,
                "isBlank": self.is_blank,
                "attachToNext": self.attach_to_next,
            }
        )

    @classmethod
    def from_dict(cls, d: dict) -> "Token":
        return cls(
            text=d["text"],
            is_blank=d.get("isBlank"),
            attach_to_next=d.get("attachToNext"),
        )


@dataclass
class BinaryChoice(CardData):
    """A true/false statement."""

    TYPE = "binary_choice_1"

    statement: str
    is_true: bool

    def to_dict(self) -> dict:
        return {"type": self.TYPE, "statement": self.statement, "isTrue": self.is_true}

    @classmethod
    def from_dict(cls, d: dict) -> "BinaryChoice":
        return cls(statement=d["statement"], is_true=d["isTrue"])


@dataclass
class FillInTheBlank(CardData):
    """A sentence with one or more blanks to fill in, plus optional distractor words."""

    TYPE = "fill_in_the_blank_1"

    tokens: List[Token] = field(default_factory=list)
    distractor_tokens: List[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "type": self.TYPE,
            "tokens": [t.to_dict() for t in self.tokens],
            "distractorTokens": list(self.distractor_tokens),
        }

    @classmethod
    def from_dict(cls, d: dict) -> "FillInTheBlank":
        return cls(
            tokens=[Token.from_dict(t) for t in d.get("tokens", [])],
            distractor_tokens=list(d.get("distractorTokens", [])),
        )

    @classmethod
    def from_text(cls, text: str, blanks: List[str], distractor_tokens: Optional[List[str]] = None) -> "FillInTheBlank":
        """
        Convenience builder: split ``text`` on ``{}`` placeholders, filling each
        one from ``blanks`` in order.

            FillInTheBlank.from_text("The {} sat on the {}.", ["cat", "mat"])
        """
        parts = text.split("{}")
        if len(parts) - 1 != len(blanks):
            raise ValueError(
                f"Text has {len(parts) - 1} '{{}}' placeholders but {len(blanks)} blanks were given."
            )
        tokens: List[Token] = []
        for i, chunk in enumerate(parts):
            if chunk:
                tokens.append(Token(text=chunk))
            if i < len(blanks):
                tokens.append(Token(text=blanks[i], is_blank=True))
        return cls(tokens=tokens, distractor_tokens=distractor_tokens or [])


@dataclass
class Flashcard(CardData):
    """A classic front/back flashcard."""

    TYPE = "flashcard_1"

    front: str
    back: str
    random: bool = False

    def to_dict(self) -> dict:
        return {
            "type": self.TYPE,
            "front": self.front,
            "back": self.back,
            "random": self.random,
        }

    @classmethod
    def from_dict(cls, d: dict) -> "Flashcard":
        return cls(front=d["front"], back=d["back"], random=d.get("random", False))


@dataclass
class MatchingPairs(CardData):
    """A set of left -> right pairs the user has to match up."""

    TYPE = "matching_pairs_1"

    pairs: Dict[str, str] = field(default_factory=dict)

    def to_dict(self) -> dict:
        return {"type": self.TYPE, "pairs": dict(self.pairs)}

    @classmethod
    def from_dict(cls, d: dict) -> "MatchingPairs":
        return cls(pairs=dict(d.get("pairs", {})))


@dataclass
class MultipleChoice(CardData):
    """A question with one correct answer and some wrong ones."""

    TYPE = "multiple_choice_1"

    question: str
    answer: str
    distractor_answers: List[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "type": self.TYPE,
            "question": self.question,
            "answer": self.answer,
            "distractorAnswers": list(self.distractor_answers),
        }

    @classmethod
    def from_dict(cls, d: dict) -> "MultipleChoice":
        return cls(
            question=d["question"],
            answer=d["answer"],
            distractor_answers=list(d.get("distractorAnswers", [])),
        )


@dataclass
class Note(CardData):
    """A plain informational note (no quizzing)."""

    TYPE = "note_1"

    content: str

    def to_dict(self) -> dict:
        return {"type": self.TYPE, "content": self.content}

    @classmethod
    def from_dict(cls, d: dict) -> "Note":
        return cls(content=d["content"])


@dataclass
class SentenceBuilder(CardData):
    """Put the words of ``sequence`` in order, ignoring the ``distractor`` words."""

    TYPE = "sentence_builder_1"

    sequence: List[str] = field(default_factory=list)
    distractor: List[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "type": self.TYPE,
            "sequence": list(self.sequence),
            "distractor": list(self.distractor),
        }

    @classmethod
    def from_dict(cls, d: dict) -> "SentenceBuilder":
        return cls(sequence=list(d.get("sequence", [])), distractor=list(d.get("distractor", [])))


# ---------------------------------------------------------------------------
# Spaced-repetition user data
# ---------------------------------------------------------------------------

@dataclass
class CardUserData:
    """Per-user review/scheduling state for a card (one.felsen.auraquiz.data.ExportCardUserData)."""

    due_date: int
    last_review: int
    difficulty: float
    stability: float
    creation_timestamp: int
    updated_timestamp: int

    def to_dict(self) -> dict:
        return {
            "dueDate": self.due_date,
            "lastReview": self.last_review,
            "difficulty": self.difficulty,
            "stability": self.stability,
            "creationTimestamp": self.creation_timestamp,
            "updatedTimestamp": self.updated_timestamp,
        }

    @classmethod
    def from_dict(cls, d: dict) -> "CardUserData":
        return cls(
            due_date=d["dueDate"],
            last_review=d["lastReview"],
            difficulty=d["difficulty"],
            stability=d["stability"],
            creation_timestamp=d["creationTimestamp"],
            updated_timestamp=d["updatedTimestamp"],
        )


# ---------------------------------------------------------------------------
# Card / Deck / ExportFile
# ---------------------------------------------------------------------------

@dataclass
class Card:
    """One card (one.felsen.auraquiz.data.ExportCard)."""

    data: CardData
    id: Optional[str] = None
    priority: Optional[int] = None
    title: Optional[str] = None
    hint: Optional[str] = None
    explanation: Optional[str] = None
    active: Optional[bool] = None
    creation_timestamp: Optional[int] = None
    updated_timestamp: Optional[int] = None
    user_data: Optional[CardUserData] = None

    def to_dict(self) -> dict:
        d = _omit_none(
            {
                "id": self.id,
                "priority": self.priority,
                "title": self.title,
                "hint": self.hint,
                "explanation": self.explanation,
                "active": self.active,
                "creationTimestamp": self.creation_timestamp,
                "updatedTimestamp": self.updated_timestamp,
                "userData": self.user_data.to_dict() if self.user_data is not None else None,
            }
        )
        d["data"] = self.data.to_dict()
        return d

    @classmethod
    def from_dict(cls, d: dict) -> "Card":
        user_data = d.get("userData")
        return cls(
            data=CardData.parse(d["data"]),
            id=d.get("id"),
            priority=d.get("priority"),
            title=d.get("title"),
            hint=d.get("hint"),
            explanation=d.get("explanation"),
            active=d.get("active"),
            creation_timestamp=d.get("creationTimestamp"),
            updated_timestamp=d.get("updatedTimestamp"),
            user_data=CardUserData.from_dict(user_data) if user_data is not None else None,
        )


@dataclass
class Deck:
    """One deck (one.felsen.auraquiz.data.ExportDeck)."""

    name: str
    id: Optional[str] = None
    description: Optional[str] = None
    authors: Optional[str] = None
    active: Optional[bool] = None
    creation_timestamp: Optional[int] = None
    updated_timestamp: Optional[int] = None
    cards: List[Card] = field(default_factory=list)

    # -- serialization -----------------------------------------------------

    def to_dict(self) -> dict:
        d = _omit_none(
            {
                "id": self.id,
                "description": self.description,
                "authors": self.authors,
                "active": self.active,
                "creationTimestamp": self.creation_timestamp,
                "updatedTimestamp": self.updated_timestamp,
            }
        )
        d["name"] = self.name
        d["cards"] = [c.to_dict() for c in self.cards]
        return d

    @classmethod
    def from_dict(cls, d: dict) -> "Deck":
        return cls(
            name=d["name"],
            id=d.get("id"),
            description=d.get("description"),
            authors=d.get("authors"),
            active=d.get("active"),
            creation_timestamp=d.get("creationTimestamp"),
            updated_timestamp=d.get("updatedTimestamp"),
            cards=[Card.from_dict(c) for c in d.get("cards") or []],
        )

    # -- convenience for building decks -------------------------------------

    def add_card(self, data: CardData, **card_kwargs: Any) -> Card:
        """Wrap any CardData instance in a Card, append it, and return it."""
        card = Card(data=data, **card_kwargs)
        self.cards.append(card)
        return card

    def add_flashcard(self, front: str, back: str, random: bool = False, **card_kwargs: Any) -> Card:
        return self.add_card(Flashcard(front=front, back=back, random=random), **card_kwargs)

    def add_multiple_choice(
            self, question: str, answer: str, distractors: List[str], **card_kwargs: Any
    ) -> Card:
        return self.add_card(
            MultipleChoice(question=question, answer=answer, distractor_answers=distractors),
            **card_kwargs,
        )

    def add_binary_choice(self, statement: str, is_true: bool, **card_kwargs: Any) -> Card:
        return self.add_card(BinaryChoice(statement=statement, is_true=is_true), **card_kwargs)

    def add_fill_in_the_blank(
            self, text: str, blanks: List[str], distractor_tokens: Optional[List[str]] = None, **card_kwargs: Any
    ) -> Card:
        return self.add_card(
            FillInTheBlank.from_text(text, blanks, distractor_tokens), **card_kwargs
        )

    def add_matching_pairs(self, pairs: Dict[str, str], **card_kwargs: Any) -> Card:
        return self.add_card(MatchingPairs(pairs=pairs), **card_kwargs)

    def add_note(self, content: str, **card_kwargs: Any) -> Card:
        return self.add_card(Note(content=content), **card_kwargs)

    def add_sentence_builder(
            self, sequence: List[str], distractor: Optional[List[str]] = None, **card_kwargs: Any
    ) -> Card:
        return self.add_card(
            SentenceBuilder(sequence=sequence, distractor=distractor or []), **card_kwargs
        )


@dataclass
class ExportFile:
    """The top-level export document (one.felsen.auraquiz.data.ExportFile)."""

    export_timestamp: Optional[int] = None
    decks: List[Deck] = field(default_factory=list)

    def to_dict(self) -> dict:
        return _omit_none(
            {
                "exportTimestamp": self.export_timestamp,
                "decks": [d.to_dict() for d in self.decks],
            }
        )

    @classmethod
    def from_dict(cls, d: dict) -> "ExportFile":
        return cls(
            export_timestamp=d.get("exportTimestamp"),
            decks=[Deck.from_dict(deck) for deck in d.get("decks") or []],
        )

    # -- convenience ---------------------------------------------------------

    def new_deck(self, name: str, **deck_kwargs: Any) -> Deck:
        """Create a new Deck, append it to this export, and return it."""
        deck = Deck(name=name, **deck_kwargs)
        self.decks.append(deck)
        return deck


# ---------------------------------------------------------------------------
# top-level load / save
# ---------------------------------------------------------------------------

def loads(text: str) -> ExportFile:
    """Parse an ExportFile from a JSON string."""
    return ExportFile.from_dict(json.loads(text))


def dumps(export: ExportFile, indent: Optional[int] = 2) -> str:
    """Serialize an ExportFile to a JSON string."""
    return json.dumps(export.to_dict(), indent=indent, ensure_ascii=False)


def load(path: Union[str, Path]) -> ExportFile:
    """Read an ExportFile from a .json file on disk."""
    with open(path, "r", encoding="utf-8") as f:
        return ExportFile.from_dict(json.load(f))


def save(export: ExportFile, path: Union[str, Path], indent: Optional[int] = 2) -> None:
    """Write an ExportFile to a .json file on disk."""
    with open(path, "w", encoding="utf-8") as f:
        f.write(dumps(export, indent=indent))


# ---------------------------------------------------------------------------
# Updating this library when the schema changes
# ---------------------------------------------------------------------------
#
# This file intentionally hard-codes the shape of the schema rather than
# parsing the .json schema file at runtime, since the schema changes rarely.
# When it does change, here's what to touch:
#
#   * New/changed field on ExportFile, ExportDeck or ExportCard
#       -> edit the matching dataclass (ExportFile, Deck, Card) above:
#          add the field, and update its to_dict/from_dict.
#
#   * New/changed field on ExportCardUserData
#       -> edit CardUserData.
#
#   * New card type (new "oneOf" entry under CardData)
#       -> add a new @dataclass subclassing CardData, set TYPE = "..."
#          (matching the JSON "type" const), implement to_dict/from_dict.
#          It is auto-registered by name -- nothing else to wire up.
#          Optionally add a Deck.add_<new_type>(...) convenience method.
#
#   * Changed/removed card type
#       -> edit or remove the matching CardData subclass.
#
# All the dataclasses use plain snake_case Python attribute names, and
# to_dict()/from_dict() are what translate to/from the exact camelCase
# JSON keys the app expects -- keep that translation in sync with the
# schema's `properties` when you edit it.
