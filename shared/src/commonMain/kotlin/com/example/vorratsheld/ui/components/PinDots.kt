package com.example.vorratsheld.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.settings.AppLockSettings
import com.example.vorratsheld.ui.theme.Cream

// Punkte für die eingegebenen PIN-Ziffern, wackeln bei falschem PIN
@Composable
fun PinDots(filled: Int, isError: Boolean, shakeTrigger: Int, modifier: Modifier = Modifier) {
    val shake = remember { Animatable(0f) }

    // Kurz hin und her wackeln bei falschem PIN
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            for (target in listOf(-18f, 18f, -12f, 12f, -6f, 6f, 0f)) {
                shake.animateTo(target, tween(45))
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.offset(x = shake.value.dp)
    ) {
        repeat(AppLockSettings.PIN_LENGTH) { index ->
            val isFilled = index < filled
            val color by animateColorAsState(
                when {
                    isError -> Color(0xFFFFB4AB)
                    isFilled -> Cream
                    else -> Color.Transparent
                }
            )
            val scale by animateFloatAsState(if (isFilled) 1.15f else 1f)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .scale(scale)
                    .background(color, CircleShape)
                    .border(2.dp, if (isError) Color(0xFFFFB4AB) else Cream, CircleShape)
            )
        }
    }
}
