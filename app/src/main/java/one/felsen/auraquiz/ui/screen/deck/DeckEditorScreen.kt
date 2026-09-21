package one.felsen.auraquiz.ui.screen.deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeckEditorScreen(
    isEditMode: Boolean = false,
    initialName: String = "",
    initialDescription: String = "",
    initialAuthors: String = "",
    initialCreationDateMillis: Long? = null,
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSave: (name: String, description: String, authors: String, creationDateMillis: Long?) -> Unit = { _, _, _, _ -> }
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var authors by remember { mutableStateOf(initialAuthors) }

    val snackbarHostState = remember { SnackbarHostState() }

    val title = if (isEditMode) "Edit Deck" else "Create Deck"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(
                        onClick = {
                            onSave(
                                name.trim(),
                                description.trim(),
                                authors.trim(),
                                initialCreationDateMillis
                            )
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = if (isEditMode) "Save changes" else "Create deck"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        DeckEditorContent(
            innerPadding = innerPadding,
            name = name,
            onNameChange = { name = it },
            description = description,
            onDescriptionChange = { description = it },
            authors = authors,
            onAuthorsChange = { authors = it },
            creationDateMillis = initialCreationDateMillis
        )
    }
}

@Composable
private fun DeckEditorContent(
    innerPadding: PaddingValues,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    authors: String,
    onAuthorsChange: (String) -> Unit,
    creationDateMillis: Long?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Deck Details",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            placeholder = { Text("e.g. Biology 101") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            label = { Text("Description") },
            placeholder = { Text("What is this deck about?") }
        )

        OutlinedTextField(
            value = authors,
            onValueChange = onAuthorsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Author(s)") },
            placeholder = { Text("e.g. Jane Doe, John Smith") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Text("Creation Date: ${creationDateMillis.toPrettyDate()}")
    }
}

private fun Long?.toPrettyDate(): String {
    if (this == null) return "Not set"
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}
