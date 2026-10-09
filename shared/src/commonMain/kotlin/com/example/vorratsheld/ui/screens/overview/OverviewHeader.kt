package com.example.vorratsheld.ui.screens.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.overview_greeting
import com.example.vorratsheld.resources.overview_greeting_anonymous
import com.example.vorratsheld.resources.overview_settings
import com.example.vorratsheld.resources.overview_shopping_list
import com.example.vorratsheld.resources.overview_subtitle
import com.example.vorratsheld.resources.stat_expired
import com.example.vorratsheld.resources.stat_soon
import com.example.vorratsheld.resources.stat_total
import com.example.vorratsheld.ui.components.ProfileAvatar
import com.example.vorratsheld.ui.theme.HeaderGreen
import org.jetbrains.compose.resources.stringResource

// Grüner Kopf: Begrüßung, Avatar, Schnellzugriffe und drei Zahlen zum Vorrat
@Composable
fun OverviewHeader(
    firstName: String,
    fullName: String,
    photoBase64: String?,
    expiredCount: Int,
    soonCount: Int,
    totalCount: Int,
    onShoppingListClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(HeaderGreen))
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    photoBase64 = photoBase64,
                    name = fullName,
                    size = 48.dp,
                    modifier = Modifier.clickable(onClick = onSettingsClick)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = if (firstName.isNotBlank()) {
                            stringResource(Res.string.overview_greeting, firstName)
                        } else {
                            stringResource(Res.string.overview_greeting_anonymous)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBF6EA)
                    )
                    Text(
                        text = stringResource(Res.string.overview_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFBF6EA).copy(alpha = 0.8f)
                    )
                }
                HeaderIconButton(Icons.Filled.ShoppingCart, stringResource(Res.string.overview_shopping_list), onShoppingListClick)
                Spacer(Modifier.size(8.dp))
                HeaderIconButton(Icons.Filled.Settings, stringResource(Res.string.overview_settings), onSettingsClick)
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(stringResource(Res.string.stat_expired), expiredCount, Color(0xFFFFDAD6), Color(0xFF93000A), Modifier.weight(1f))
                StatTile(stringResource(Res.string.stat_soon), soonCount, Color(0xFFFFF3CD), Color(0xFF7A5B00), Modifier.weight(1f))
                StatTile(stringResource(Res.string.stat_total), totalCount, Color(0xFFFBF6EA), Color(0xFF2F5530), Modifier.weight(1f))
            }
        }
    }
}
