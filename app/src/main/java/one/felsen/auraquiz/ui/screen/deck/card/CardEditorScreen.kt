package one.felsen.auraquiz.ui.screen.deck.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardEditorScreen(
    isEditing: Boolean = false,
    onBack: () -> Unit,
    onSave: (CardType, Map<String, String>) -> Unit, // In a real app, pass your sealed class here
    onDelete: () -> Unit
) {
    // State for the type of card being created/edited
    var selectedCardType by remember { mutableStateOf(CardType.FLASHCARD) }
    
    // Scroll state for smaller screens
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Edit Card" else "New Card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { /* TODO: Map UI state to your CardContent sealed class and save */ }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. The Type Selector
            CardTypeSelector(
                selectedType = selectedCardType,
                onTypeSelected = { selectedCardType = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. The Dynamic Form
            CardEditorBody(selectedCardType)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CardEditorBody(selectedCardType: CardType) {
    // Hoist these to ViewModel in production
    var title by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. The Common Data (Title, Hint, Explanation)
        CommonCardFields(
            title = title, onTitleChange = { title = it },
            hint = hint, onHintChange = { hint = it },
            explanation = explanation, onExplanationChange = { explanation = it }
        )

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        // 2. The Type-Specific Data (Reactive Content)
        when (selectedCardType) {
            CardType.FLASHCARD -> FlashcardForm()
            CardType.SENTENCE_BUILDER -> SentenceBuilderForm()
            CardType.NOTE -> NoteForm()
            CardType.MULTIPLE_CHOICE -> MultipleChoiceForm()
            CardType.BINARY_CHOICE -> BinaryChoiceForm()
            CardType.FILL_IN_THE_BLANK -> FillInTheBlankForm()
            CardType.MATCHING_PAIRS -> MatchingPairsForm()
        }
    }
}