package com.example.vorratsheld.ui.screens.productinfo

import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Runder Button über dem Produktbild
@Composable
fun HeaderButton(icon: ImageVector, description: String, onClick: () -> Unit, tint: Color = Color.White) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.35f),
            contentColor = tint
        )
    ) {
        Icon(icon, contentDescription = description)
    }
}
