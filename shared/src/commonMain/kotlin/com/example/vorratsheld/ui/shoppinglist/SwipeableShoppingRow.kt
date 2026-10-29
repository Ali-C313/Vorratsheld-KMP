package com.example.vorratsheld.ui.shoppinglist

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.model.ShoppingItem
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.ui.components.SwipeActionButton
import com.example.vorratsheld.ui.components.SwipeRevealBox
import org.jetbrains.compose.resources.stringResource

// Einkaufszeile, die sich zum Löschen wischen lässt
@Composable
fun SwipeableShoppingRow(
    item: ShoppingItem,
    isOpen: Boolean,
    onOpenChange: (Boolean) -> Unit,
    onToggle: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeRevealBox(
        isOpen = isOpen,
        onOpenChange = onOpenChange,
        actionsWidth = 80.dp,
        actions = {
            SwipeActionButton(
                icon = Icons.Filled.Delete,
                label = stringResource(Res.string.action_delete),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = onDelete
            )
        },
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        ShoppingRow(item = item, onToggle = onToggle, onQuantityChange = onQuantityChange)
    }
}
