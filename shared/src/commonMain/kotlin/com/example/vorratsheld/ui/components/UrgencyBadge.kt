package com.example.vorratsheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.urgency_expired
import com.example.vorratsheld.resources.urgency_ok
import com.example.vorratsheld.resources.urgency_short
import com.example.vorratsheld.resources.urgency_soon
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource

// Farbiges Schild für abgelaufene oder bald ablaufende Produkte
@Composable
fun UrgencyBadge(urgency: ExpiryUrgency, modifier: Modifier = Modifier) {
    val (background, content, label) = when (urgency) {
        ExpiryUrgency.ABGELAUFEN -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            stringResource(Res.string.urgency_expired)
        )
        ExpiryUrgency.BALD_ABLAUFEND -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            stringResource(Res.string.urgency_soon)
        )
        ExpiryUrgency.IN_KUERZE -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF7A5B00),
            stringResource(Res.string.urgency_short)
        )
        ExpiryUrgency.UNKRITISCH -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            stringResource(Res.string.urgency_ok)
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

@Preview(showBackground = true)
@Composable
private fun UrgencyBadgePreview() {
    VorratsheldTheme {
        UrgencyBadge(urgency = ExpiryUrgency.BALD_ABLAUFEND, modifier = Modifier.padding(16.dp))
    }
}
