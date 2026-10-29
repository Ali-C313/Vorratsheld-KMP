package com.example.vorratsheld.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.Amber
import com.example.vorratsheld.ui.theme.Brick
import com.example.vorratsheld.ui.theme.Green

// Seite 4: Einkaufswagen mit Stern und Haken
@Composable
fun ShoppingIllustration() {
    val infinite = rememberInfiniteTransition()
    val bounce by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse)
    )
    Box(contentAlignment = Alignment.Center) {
        BigIcon(Icons.Filled.ShoppingCart, Brick)
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = Amber,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(44.dp)
                .graphicsLayer {
                    scaleX = bounce
                    scaleY = bounce
                }
        )
        Surface(
            shape = CircleShape,
            color = Green,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(40.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
        }
    }
}
