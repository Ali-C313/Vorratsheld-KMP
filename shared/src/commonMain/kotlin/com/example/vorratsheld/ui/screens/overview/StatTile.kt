package com.example.vorratsheld.ui.screens.overview

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// Kachel mit hochzählender Zahl
@Composable
fun StatTile(label: String, count: Int, background: Color, content: Color, modifier: Modifier = Modifier) {
    val animatedCount by animateIntAsState(count, tween(800))
    Surface(
        color = background,
        contentColor = content,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(text = animatedCount.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatTilePreview() {
    VorratsheldTheme {
        StatTile(
            label = "Bald",
            count = 3,
            background = Color(0xFFFFF3CD),
            content = Color(0xFF7A5B00),
            modifier = Modifier.padding(16.dp)
        )
    }
}
