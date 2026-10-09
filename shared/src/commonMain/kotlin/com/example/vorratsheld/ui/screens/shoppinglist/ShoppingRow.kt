package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.model.ShoppingItem
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// Ganze Zeile tippbar zum Abhaken, Menge per +/−
@Composable
fun ShoppingRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    val contentAlpha by animateFloatAsState(if (item.checked) 0.5f else 1f)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (item.checked) 0.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onToggle)
                .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp)
        ) {
            CheckCircle(checked = item.checked)

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .alpha(contentAlpha)
            )

            if (item.checked) {
                Text(
                    text = "${item.quantity}×",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
            } else {
                QuantityStepper(quantity = item.quantity, onQuantityChange = onQuantityChange)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShoppingRowPreview() {
    VorratsheldTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ShoppingRow(item = ShoppingItem(id = "1", name = "Eier", quantity = 10), onToggle = {}, onQuantityChange = {})
            ShoppingRow(item = ShoppingItem(id = "2", name = "Mehl", checked = true), onToggle = {}, onQuantityChange = {})
        }
    }
}
