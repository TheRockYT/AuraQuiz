package one.felsen.auraquiz.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.AppDatabase
import one.felsen.auraquiz.data.NoteRepository
import one.felsen.auraquiz.data.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("AuraQuiz") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Welcome to AuraQuiz")

            val context = LocalContext.current.applicationContext

            val database = AppDatabase.getInstance(context)

            val notes = viewModel {NotesViewModel(NoteRepository(database.noteDao())) }

            val noteList by notes.tasks.collectAsStateWithLifecycle(listOf())

            noteList.forEach {
                Text(it.title)
            }

            var text by remember { mutableStateOf("") }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter your name") },
                placeholder = { Text("e.g., John Doe") },
                singleLine = true
            )

            Button(onClick = {
                notes.addTask(text)
            }) {
                Text("Add")
            }
        }
    }
}