package com.example.vorratsheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.favorite
import com.example.vorratsheld.ui.theme.FavoriteYellow
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.DateHelper
import org.jetbrains.compose.resources.stringResource

// Karte für ein Produkt in der Übersicht: Bild, Name, Menge, Ablauf, Favorit
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urgency = DateHelper.expiryUrgencyFor(product.expiryDate)
    val daysLeft = DateHelper.daysUntilExpiry(product.expiryDate)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Produktbild oder Platzhalter-Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (product.quantity > 1) {
                        Text(
                            text = "${product.quantity}×",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
                Text(
                    text = expiryLabel(daysLeft),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ablauf-Badge nur anzeigen, wenn es bald oder schon abgelaufen ist
            if (urgency == ExpiryUrgency.BALD_ABLAUFEND || urgency == ExpiryUrgency.ABGELAUFEN) {
                UrgencyBadge(urgency = urgency, modifier = Modifier.padding(end = 4.dp))
            } else if (urgency == ExpiryUrgency.IN_KUERZE) {
                UrgencyBadge(urgency = urgency, modifier = Modifier.padding(end = 4.dp))
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = stringResource(Res.string.favorite),
                    tint = if (product.isFavorite) FavoriteYellow else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    VorratsheldTheme {
        ProductCard(
            product = Product(name = "Vollmilch 1L", quantity = 2, isFavorite = true, expiryDate = DateHelper.nowMillis()),
            onClick = {},
            onFavoriteToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
