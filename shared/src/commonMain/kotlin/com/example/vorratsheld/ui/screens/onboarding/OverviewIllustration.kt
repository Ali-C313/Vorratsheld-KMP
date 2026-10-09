package com.example.vorratsheld.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.storage_fridge
import com.example.vorratsheld.resources.storage_pantry
import org.jetbrains.compose.resources.stringResource

// Seite 2: kleine Produktkarten mit Ablauf-Farben, die nacheinander reinschweben
@Composable
fun OverviewIllustration() {
    val infinite = rememberInfiniteTransition()
    val float by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val items = listOf(
        Triple(stringResource(Res.string.storage_fridge), Color(0xFFBA1A1A), 0.45f),
        Triple(stringResource(Res.string.storage_pantry), Color(0xFFE8A93C), 0.7f),
        Triple(stringResource(Res.string.storage_fridge), Color(0xFF3F6B3F), 0.9f)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, (label, color, fill) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .width(210.dp)
                    .graphicsLayer { translationY = if (index % 2 == 0) float else -float }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                        Text(label, style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(6.dp))
                        Canvas(Modifier.fillMaxWidth().height(6.dp)) {
                            drawRoundRect(color.copy(alpha = 0.2f), cornerRadius = CornerRadius(size.height))
                            drawRoundRect(color, size = Size(size.width * fill, size.height), cornerRadius = CornerRadius(size.height))
                        }
                    }
                    Box(
                        Modifier
                            .padding(start = 10.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}
