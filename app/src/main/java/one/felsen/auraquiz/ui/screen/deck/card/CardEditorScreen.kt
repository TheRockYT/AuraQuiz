package one.felsen.auraquiz.ui.screen.deck.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import one.felsen.auraquiz.data.card.CardData
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardType

@Composable
fun CardEditorScreen(
    isEditing: Boolean = false, onBack: () -> Unit,
    onSave: (title: String, hint: String, explanation: String, cardData: CardData) -> Unit,
    onDelete: () -> Unit,
    initialCard: CardEntity? = null,
) {
    val data = initialCard?.data
    var selectedCardType by remember { mutableStateOf(CardType.FLASHCARD) }

    val scrollState = rememberScrollState()



    var title by remember { mutableStateOf(initialCard?.title ?: "") }
    var hint by remember { mutableStateOf(initialCard?.hint ?: "") }
    var explanation by remember { mutableStateOf(initialCard?.explanation ?: "") }

    var front by remember { mutableStateOf(if(data is CardData.Flashcard) data.front else "") }
    var back by remember { mutableStateOf(if(data is CardData.Flashcard) data.back else "") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(if (isEditing) "Edit Card" else "New Card") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }, actions = {
                if (isEditing) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Card",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                TextButton(onClick = {
                    onSave(
                        title, hint, explanation, CardData.Flashcard(
                            front = front,
                            back = back,
                        )
                    )
                }) {
                    Text("Save")
                }
            })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. The Type Selector
//            CardTypeSelector(selectedType = selectedCardType, onTypeSelected = { selectedCardType = it })

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. The Dynamic Form

            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. The Common Data (Title, Hint, Explanation)
                CommonCardFields(
                    title = title,
                    onTitleChange = { title = it },
                    hint = hint,
                    onHintChange = { hint = it },
                    explanation = explanation,
                    onExplanationChange = { explanation = it })

                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

                // 2. The Type-Specific Data

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = front,
                        onValueChange = { front = it },
                        label = { Text("Front (Prompt)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = back,
                        onValueChange = { back = it },
                        label = { Text("Back (Answer)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

//        when (selectedCardType) {
//            CardType.FLASHCARD -> FlashcardForm()
//            CardType.SENTENCE_BUILDER -> SentenceBuilderForm()
//            CardType.NOTE -> NoteForm()
//            CardType.MULTIPLE_CHOICE -> MultipleChoiceForm()
//            CardType.BINARY_CHOICE -> BinaryChoiceForm()
//            CardType.FILL_IN_THE_BLANK -> FillInTheBlankForm()
//            CardType.MATCHING_PAIRS -> MatchingPairsForm()
//        }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
