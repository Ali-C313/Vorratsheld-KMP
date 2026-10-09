package com.example.vorratsheld.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vorratsheld.ui.theme.Cream

// Runde Zifferntaste im PIN-Feld
@Composable
fun DigitKey(label: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.14f),
        contentColor = Cream,
        modifier = Modifier.size(76.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, fontSize = 30.sp, fontWeight = FontWeight.Medium)
        }
    }
}
