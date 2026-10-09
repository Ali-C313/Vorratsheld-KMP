package com.example.vorratsheld.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Seite 1: Barcode mit wanderndem Scan-Strahl
@Composable
fun ScanIllustration() {
    val infinite = rememberInfiniteTransition()
    val scanY by infinite.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val barColor = MaterialTheme.colorScheme.onSurface
    val frameColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(180.dp, 140.dp)) {
        // Rahmen-Ecken wie im Kamera-Sucher
        val corner = 28.dp.toPx()
        val stroke = 5.dp.toPx()
        listOf(
            Offset(0f, 0f) to Offset(1f, 1f),
            Offset(size.width, 0f) to Offset(-1f, 1f),
            Offset(0f, size.height) to Offset(1f, -1f),
            Offset(size.width, size.height) to Offset(-1f, -1f)
        ).forEach { (p, dir) ->
            drawLine(frameColor, p, Offset(p.x + corner * dir.x, p.y), stroke)
            drawLine(frameColor, p, Offset(p.x, p.y + corner * dir.y), stroke)
        }
        // Barcode-Striche
        val widths = listOf(3, 1, 2, 1, 3, 2, 1, 1, 3, 1, 2, 3, 1, 2, 1, 3, 1, 2)
        val unit = (size.width * 0.7f) / widths.sum().times(1.6f)
        var x = size.width * 0.15f
        widths.forEachIndexed { i, w ->
            if (i % 2 == 0) {
                drawRect(barColor, Offset(x, size.height * 0.2f), Size(w * unit, size.height * 0.6f))
            }
            x += w * unit * 1.6f
        }
        // Scan-Strahl
        val y = size.height * scanY
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xFFB8432F).copy(alpha = 0.5f), Color.Transparent),
                startY = y - 18.dp.toPx(),
                endY = y + 18.dp.toPx()
            ),
            topLeft = Offset(0f, y - 18.dp.toPx()),
            size = Size(size.width, 36.dp.toPx())
        )
        drawLine(Color(0xFFB8432F), Offset(8.dp.toPx(), y), Offset(size.width - 8.dp.toPx(), y), 3.dp.toPx())
    }
}
