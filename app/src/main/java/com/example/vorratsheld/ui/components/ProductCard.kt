package com.example.vorratsheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.enums.daysUntilExpiry
import com.example.vorratsheld.data.enums.expiryUrgencyFor

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urgency = expiryUrgencyFor(product.expiryDate)
    val daysLeft = daysUntilExpiry(product.expiryDate)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Produktbild oder Platzhalter-Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.size(40.dp)
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
                Text(text = product.name, style = MaterialTheme.typography.bodyMedium)
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
                    contentDescription = "Favorit",
                    tint = if (product.isFavorite) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UrgencyBadge(urgency: ExpiryUrgency, modifier: Modifier = Modifier) {
    val (background, content, label) = when (urgency) {
        ExpiryUrgency.ABGELAUFEN -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Abgelaufen"
        )
        ExpiryUrgency.BALD_ABLAUFEND -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Bald ablaufen"
        )
        ExpiryUrgency.IN_KUERZE -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF7A5B00),
            "Bald"
        )
        ExpiryUrgency.UNKRITISCH -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "OK"
        )
    }

    Surface(
        color = background,
        contentColor = content,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private fun expiryLabel(daysLeft: Long): String = when {
    daysLeft < 0 -> "Seit ${-daysLeft} Tag(en) abgelaufen"
    daysLeft == 0L -> "Läuft heute ab"
    daysLeft == 1L -> "Läuft morgen ab"
    else -> "Läuft ab in $daysLeft Tagen"
}