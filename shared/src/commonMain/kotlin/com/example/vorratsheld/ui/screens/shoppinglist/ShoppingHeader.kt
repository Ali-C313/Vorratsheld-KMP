package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.shopping_clear_done
import com.example.vorratsheld.resources.shopping_progress
import com.example.vorratsheld.resources.shopping_title
import com.example.vorratsheld.ui.theme.Cream
import com.example.vorratsheld.ui.theme.HeaderGreen
import org.jetbrains.compose.resources.stringResource

// Grüner Kopf mit Fortschritt wie viel schon im Korb ist
@Composable
fun ShoppingHeader(
    doneCount: Int,
    totalCount: Int,
    onBackClick: () -> Unit,
    onClearDone: () -> Unit
) {
    val progress by animateFloatAsState(if (totalCount == 0) 0f else doneCount.toFloat() / totalCount)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(HeaderGreen))
            .statusBarsPadding()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back), tint = Cream)
            }
            Text(
                text = stringResource(Res.string.shopping_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Cream,
                modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(visible = doneCount > 0, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
                FilledTonalIconButton(
                    onClick = onClearDone,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        contentColor = Cream
                    )
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = stringResource(Res.string.shopping_clear_done))
                }
            }
        }

        if (totalCount > 0) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = stringResource(Res.string.shopping_progress, doneCount, totalCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cream.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFFE8A93C),
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    }
}
