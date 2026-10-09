package com.example.vorratsheld.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// Eine Zeile in den Einstellungen: Icon, Titel, optional Untertitel, Pfeil nach rechts
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        headlineContent = { Text(title) },
        supportingContent = if (subtitle != null) {
            { Text(subtitle) }
        } else {
            null
        },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    VorratsheldTheme {
        SettingsItem(icon = Icons.Filled.Language, title = "Sprache", subtitle = "Deutsch", onClick = {})
    }
}
