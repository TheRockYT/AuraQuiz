package one.felsen.auraquiz.ui.screen.deck.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. FLASHCARD
@Composable
fun FlashcardForm() {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = front, onValueChange = { front = it },
            label = { Text("Front (Prompt)") }, modifier = Modifier.fillMaxWidth(), minLines = 3
        )
        OutlinedTextField(
            value = back, onValueChange = { back = it },
            label = { Text("Back (Answer)") }, modifier = Modifier.fillMaxWidth(), minLines = 3
        )
    }
}

// 2. SENTENCE BUILDER
@Composable
fun SentenceBuilderForm() {
    val sequence = remember { mutableStateListOf("The", "quick", "brown", "fox") }
    val distractors = remember { mutableStateListOf<String>() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DynamicListEditor(
            listTitle = "Correct Sequence (In Order)",
            items = sequence,
            onItemChange = { index, value -> sequence[index] = value },
            onAddItem = { sequence.add("") },
            onRemoveItem = { sequence.removeAt(it) }
        )
        HorizontalDivider()
        DynamicListEditor(
            listTitle = "Extra Distractor Words (Optional)",
            items = distractors,
            onItemChange = { index, value -> distractors[index] = value },
            onAddItem = { distractors.add("") },
            onRemoveItem = { distractors.removeAt(it) }
        )
    }
}

// 3. NOTE
@Composable
fun NoteForm() {
    var body by remember { mutableStateOf("") }
    OutlinedTextField(
        value = body, onValueChange = { body = it },
        label = { Text("Note Content") }, modifier = Modifier.fillMaxWidth(), minLines = 5
    )
}

// 4. MULTIPLE CHOICE
@Composable
fun MultipleChoiceForm() {
    var question by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    val distractors = remember { mutableStateListOf<String>() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = question, onValueChange = { question = it },
            label = { Text("Question") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = correctAnswer, onValueChange = { correctAnswer = it },
            label = { Text("Correct Answer") }, modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()
        DynamicListEditor(
            listTitle = "Specific Wrong Answers",
            items = distractors,
            onItemChange = { index, value -> distractors[index] = value },
            onAddItem = { distractors.add("") },
            onRemoveItem = { distractors.removeAt(it) }
        )
    }
}

// 5. BINARY CHOICE
@Composable
fun BinaryChoiceForm() {
    var statement by remember { mutableStateOf("") }
    var isTrue by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = statement, onValueChange = { statement = it },
            label = { Text("Statement") }, modifier = Modifier.fillMaxWidth(), minLines = 2
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is this statement true?", style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isTrue, onCheckedChange = { isTrue = it })
        }
    }
}

// 6. FILL-IN-THE-BLANK
@Composable
fun FillInTheBlankForm() {
    var template by remember { mutableStateOf("The capital of France is ___.") }
    var correctAnswer by remember { mutableStateOf("") }
    val distractors = remember { mutableStateListOf<String>() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = template, onValueChange = { template = it },
            label = { Text("Sentence Template (Use '___' for blank)") },
            modifier = Modifier.fillMaxWidth(), minLines = 2
        )
        OutlinedTextField(
            value = correctAnswer, onValueChange = { correctAnswer = it },
            label = { Text("Correct Word") }, modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()
        DynamicListEditor(
            listTitle = "Wrong Word Chips (Optional)",
            items = distractors,
            onItemChange = { index, value -> distractors[index] = value },
            onAddItem = { distractors.add("") },
            onRemoveItem = { distractors.removeAt(it) }
        )
    }
}

// 7. MATCHING PAIRS
@Composable
fun MatchingPairsForm() {
    val pairs = remember { mutableStateListOf(Pair("", "")) }

    DynamicPairEditor(
        pairs = pairs,
        onPairChange = { index, left, right -> pairs[index] = Pair(left, right) },
        onAddPair = { pairs.add(Pair("", "")) },
        onRemovePair = { pairs.removeAt(it) }
    )
}