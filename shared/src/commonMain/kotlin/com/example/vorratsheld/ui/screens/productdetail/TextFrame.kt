package com.example.vorratsheld.ui.screens.productdetail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Flacher Rahmen für die Datumszeile, wird grün sobald ein Datum erkannt ist
@Composable
fun TextFrame(found: Boolean, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition()
    val pulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val color = if (found) Color(0xFF7BC67B) else Color.White.copy(alpha = pulse)

    Canvas(modifier = modifier.size(300.dp, 110.dp)) {
        val corner = 28.dp.toPx()
        val stroke = 5.dp.toPx()
        listOf(
            Offset(0f, 0f) to Offset(1f, 1f),
            Offset(size.width, 0f) to Offset(-1f, 1f),
            Offset(0f, size.height) to Offset(1f, -1f),
            Offset(size.width, size.height) to Offset(-1f, -1f)
        ).forEach { (p, dir) ->
            drawLine(color, p, Offset(p.x + corner * dir.x, p.y), stroke)
            drawLine(color, p, Offset(p.x, p.y + corner * dir.y), stroke)
        }
    }
}
