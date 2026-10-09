package com.example.vorratsheld.ui.screens.overview

import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Halbtransparenter Button im grünen Kopf
@Composable
fun HeaderIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.18f),
            contentColor = Color(0xFFFBF6EA)
        )
    ) {
        Icon(icon, contentDescription = description)
    }
}
