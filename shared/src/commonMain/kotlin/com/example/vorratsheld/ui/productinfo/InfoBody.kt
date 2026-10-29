package com.example.vorratsheld.ui.productinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.info_added
import com.example.vorratsheld.resources.info_barcode
import com.example.vorratsheld.resources.product_expiry
import com.example.vorratsheld.resources.product_location
import com.example.vorratsheld.resources.product_quantity
import com.example.vorratsheld.resources.storage_fridge
import com.example.vorratsheld.resources.storage_pantry
import com.example.vorratsheld.util.DateHelper
import org.jetbrains.compose.resources.stringResource

// Weißes Blatt mit Name, Status und den Daten als Kacheln
@Composable
fun InfoBody(product: Product, modifier: Modifier = Modifier) {
    val isFridge = product.location == StorageLocation.KUEHLSCHRANK
    val locationLabel = stringResource(if (isFridge) Res.string.storage_fridge else Res.string.storage_pantry)

    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Pill(
                    icon = if (isFridge) Icons.Filled.Kitchen else Icons.Filled.Inventory2,
                    text = locationLabel,
                    background = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer
                )
                ExpiryPill(product.expiryDate)
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile(
                    icon = Icons.Filled.Numbers,
                    label = stringResource(Res.string.product_quantity),
                    value = "${product.quantity}×",
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    icon = Icons.Filled.CalendarMonth,
                    label = stringResource(Res.string.product_expiry),
                    value = DateHelper.formatDate(product.expiryDate),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile(
                    icon = if (isFridge) Icons.Filled.Kitchen else Icons.Filled.Inventory2,
                    label = stringResource(Res.string.product_location),
                    value = locationLabel,
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    icon = Icons.Filled.Event,
                    label = stringResource(Res.string.info_added),
                    value = DateHelper.formatLocalDate(product.addedAt),
                    modifier = Modifier.weight(1f)
                )
            }
            if (product.barcode.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                InfoTile(
                    icon = Icons.Filled.QrCode,
                    label = stringResource(Res.string.info_barcode),
                    value = product.barcode,
                    monospace = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
