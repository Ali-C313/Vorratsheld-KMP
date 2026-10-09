package com.example.vorratsheld.ui.screens.onboarding

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

// Seite 3: Glocke, die bimmelt
@Composable
fun ReminderIllustration() {
    val infinite = rememberInfiniteTransition()
    val ring by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 1600
                0f at 0
                -18f at 100
                16f at 200
                -12f at 300
                8f at 400
                0f at 520
                0f at 1600
            }
        )
    )
    BigIcon(Icons.Filled.NotificationsActive, Color(0xFFC07A45), rotation = ring)
}
