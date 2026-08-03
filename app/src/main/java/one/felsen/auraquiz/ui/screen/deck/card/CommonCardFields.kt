package one.felsen.auraquiz.ui.screen.deck.card

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// --- COMMON FIELDS ---
@Composable
fun CommonCardFields(
    title: String, onTitleChange: (String) -> Unit,
    hint: String, onHintChange: (String) -> Unit,
    explanation: String, onExplanationChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = title, onValueChange = onTitleChange,
            label = { Text("Card Title (Required)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = hint, onValueChange = onHintChange,
            label = { Text("Hint (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = explanation, onValueChange = onExplanationChange,
            label = { Text("Explanation (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
    }
}

// --- DYNAMIC LIST EDITOR ---
@Composable
fun DynamicListEditor(
    listTitle: String,
    items: List<String>,
    onItemChange: (index: Int, newValue: String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (index: Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(listTitle, style = MaterialTheme.typography.titleMedium)
        
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = item,
                    onValueChange = { onItemChange(index, it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = { onRemoveItem(index) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        
        TextButton(onClick = onAddItem) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Item")
        }
    }
}

// --- DYNAMIC PAIR EDITOR (For Matching Pairs) ---
@Composable
fun DynamicPairEditor(
    pairs: List<Pair<String, String>>,
    onPairChange: (index: Int, left: String, right: String) -> Unit,
    onAddPair: () -> Unit,
    onRemovePair: (index: Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Matching Pairs", style = MaterialTheme.typography.titleMedium)
        
        pairs.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pair.first,
                    onValueChange = { onPairChange(index, it, pair.second) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Left side") }
                )
                OutlinedTextField(
                    value = pair.second,
                    onValueChange = { onPairChange(index, pair.first, it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Right side") }
                )
                IconButton(onClick = { onRemovePair(index) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove pair", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        TextButton(onClick = onAddPair) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Pair")
        }
    }
}