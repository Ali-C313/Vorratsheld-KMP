package com.example.vorratsheld.ui.scan

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.Amber

// Sucher-Ecken mit Scan-Linie, die hoch und runter läuft
@Composable
fun ScanFrame(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition()
    val lineY by infinite.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Canvas(modifier = modifier.size(280.dp, 180.dp)) {
        val corner = 36.dp.toPx()
        val stroke = 5.dp.toPx()
        listOf(
            Offset(0f, 0f) to Offset(1f, 1f),
            Offset(size.width, 0f) to Offset(-1f, 1f),
            Offset(0f, size.height) to Offset(1f, -1f),
            Offset(size.width, size.height) to Offset(-1f, -1f)
        ).forEach { (p, dir) ->
            drawLine(Color.White, p, Offset(p.x + corner * dir.x, p.y), stroke)
            drawLine(Color.White, p, Offset(p.x, p.y + corner * dir.y), stroke)
        }
        val y = size.height * lineY
        drawLine(Amber, Offset(16.dp.toPx(), y), Offset(size.width - 16.dp.toPx(), y), 3.dp.toPx())
    }
}
