package com.example.vorratsheld.ui.productinfo

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.ui.components.expiryLabel
import com.example.vorratsheld.ui.theme.AmberDark
import com.example.vorratsheld.ui.theme.AmberLight
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.DateHelper

// Ablauf-Hinweis als farbige Pille
@Composable
fun ExpiryPill(expiryDate: Long) {
    val text = expiryLabel(DateHelper.daysUntilExpiry(expiryDate))
    val (background, content) = when (DateHelper.expiryUrgencyFor(expiryDate)) {
        ExpiryUrgency.ABGELAUFEN, ExpiryUrgency.BALD_ABLAUFEND ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        ExpiryUrgency.IN_KUERZE -> AmberLight to AmberDark
        ExpiryUrgency.UNKRITISCH -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Pill(icon = null, text = text, background = background, content = content)
}

@Preview(showBackground = true)
@Composable
private fun ExpiryPillPreview() {
    VorratsheldTheme {
        ExpiryPill(expiryDate = DateHelper.nowMillis())
    }
}
