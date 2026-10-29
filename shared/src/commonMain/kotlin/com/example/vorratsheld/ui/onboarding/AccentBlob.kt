package com.example.vorratsheld.ui.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

// Weich atmender Farbkreis hinter jeder Illustration
@Composable
fun AccentBlob(color: Color) {
    val infinite = rememberInfiniteTransition()
    val breathe by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Box(
        modifier = Modifier
            .size(260.dp)
            .graphicsLayer {
                scaleX = breathe
                scaleY = breathe
            }
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.06f))))
    )
}
