package com.example.vorratsheld.ui.screens.language

import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Eine Sprache zum Auswählen
@Composable
fun LanguageRow(name: String, selected: Boolean, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.selectable(selected = selected, onClick = onClick),
        headlineContent = { Text(name) },
        leadingContent = { RadioButton(selected = selected, onClick = null) }
    )
}
