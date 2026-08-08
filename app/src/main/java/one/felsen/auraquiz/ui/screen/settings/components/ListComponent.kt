package one.felsen.auraquiz.ui.screen.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> MultiSelectableList(
    items: List<T>,
    selectedItems: Set<T>,
    indexId: (item: T) -> Any,
    onItemClicked: (item: T) -> Unit,
    content: @Composable (index: Int, item: T) -> Unit
) {
    ListComponent(
        items = items,
        indexId = indexId,
        onItemClicked = onItemClicked,
    ) { index, item ->
        val isSelected = selectedItems.contains(item)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content(index, item)
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = null
            )
        }
    }
}

@Composable
fun <T> ListComponent(
    items: List<T>,
    indexId: (item: T) -> Any,
    onItemClicked: (item: T) -> Unit,
    content: @Composable (index: Int, item: T) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> indexId(item) }
        ) { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = {
                    onItemClicked(item)
                }) {
                content(index, item)
            }
        }
    }
}