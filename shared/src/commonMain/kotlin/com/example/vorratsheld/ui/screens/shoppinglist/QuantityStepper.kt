package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// Menge mit Minus und Plus ändern
@Composable
fun QuantityStepper(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(50)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onQuantityChange(-1) },
                enabled = quantity > 1,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(width = 20.dp, height = 20.dp)
            )
            IconButton(
                onClick = { onQuantityChange(1) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantityStepperPreview() {
    VorratsheldTheme {
        QuantityStepper(quantity = 2, onQuantityChange = {})
    }
}
